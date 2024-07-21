package de.obsidiancloud.node.module;

import com.google.common.collect.ImmutableList;
import de.obsidiancloud.node.ObsidianCloudNode;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The module loader loads and unloads modules */
public class ModuleLoader {
    private static final List<ModuleManifest> manifests = new CopyOnWriteArrayList<>();

    /**
     * Gets all loaded manifests
     *
     * @return All loaded manifests
     */
    public static @NotNull List<ModuleManifest> getManifests() {
        return manifests;
    }

    /**
     * Gets all loaded modules
     *
     * @return All loaded modules
     */
    @SuppressWarnings("unused")
    public static @NotNull List<Module> getModules() {
        ImmutableList.Builder<Module> modules = ImmutableList.builder();
        for (ModuleManifest manifest : manifests) {
            modules.add(manifest.getInstance());
        }
        return modules.build();
    }

    /**
     * Gets a module by its name
     *
     * @param name The name of the module
     * @return The module or null if the module is not loaded
     */
    public static @Nullable Module getModule(@NotNull String name) {
        for (ModuleManifest manifest : manifests) {
            if (manifest.getName().equals(name)) {
                return manifest.getInstance();
            }
        }
        return null;
    }

    /**
     * Loads all modules from a directory
     *
     * @param directory The directory to load
     * @throws IOException If an error occurs while loading the modules
     */
    public static void loadModules(@NotNull Path directory, @NotNull Logger logger)
            throws IOException {
        List<Path> files;
        try (Stream<Path> stream = Files.list(directory)) {
            files = stream.toList();
        }
        List<ModuleManifest> manifests = new ArrayList<>();
        for (Path file : files) {
            if (!file.getFileName().toString().endsWith(".jar")) {
                continue;
            }
            try {
                manifests.add(ModuleManifest.loadManifest(file));
            } catch (Throwable exception) {
                logger.log(Level.SEVERE, "An error occurred while loading " + file, exception);
            }
        }
        ModuleLoader.manifests.addAll(manifests);

        manifests.removeIf(
                manifest -> {
                    if (manifest.getDependencies().contains(manifest.getName())) {
                        logger.warning(
                                "Module \"" + manifest.getName() + "\" has a dependency to itself");
                        return true;
                    }
                    for (ModuleManifest manifest2 : manifests) {
                        if (manifest.getDependencies().contains(manifest2.getName())
                                && manifest2.getDependencies().contains(manifest.getName())) {
                            logger.warning(
                                    "Module \""
                                            + manifest.getName()
                                            + "\" and module \""
                                            + manifest2.getName()
                                            + "\" have a dependency to each other");
                            return true;
                        }
                    }
                    return false;
                });

        for (ModuleManifest manifest : sortModules(manifests)) {
            ModuleLoader.enableModule(manifest, logger);
        }
    }

    /** Disables all modules */
    public static void disableModules(@NotNull Logger logger) {
        List<ModuleManifest> sorted = new ArrayList<>(manifests);
        Collections.reverse(sorted);
        for (ModuleManifest manifest : sorted) {
            if (manifest.isEnabled()) disableModule(manifest, logger);
        }
    }

    private static @NotNull List<ModuleManifest> sortModules(
            @NotNull List<ModuleManifest> manifests) {
        List<ModuleManifest> sorted = new ArrayList<>();
        for (ModuleManifest manifest : manifests) {
            int priority = 0;
            for (int i = 0; i < sorted.size(); i++) {
                ModuleManifest manifest2 = sorted.get(i);
                if (manifest2.getDependencies().contains(manifest.getName())
                        || manifest2.getSoftDependencies().contains(manifest.getName())) {
                    priority = i;
                    break;
                }
            }
            sorted.add(priority, manifest);
        }
        return sorted;
    }

    /**
     * Enables a module
     *
     * @param manifest The manifest of the module to enable
     */
    public static void enableModule(@NotNull ModuleManifest manifest, Logger logger) {
        try {
            for (String dependency : manifest.getDependencies()) {
                if (getModule(dependency) == null) {
                    logger.warning(
                            "Module \""
                                    + manifest.getName()
                                    + "\" has a dependency to \""
                                    + dependency
                                    + "\" which is not loaded");
                    return;
                }
            }
            logger.info("Enabling module \"" + manifest.getName() + "\"");
            if (!manifest.isEnabled()) {
                loadClasses(manifest);
            }
            manifest.getInstance().onEnable();
        } catch (Exception exception) {
            logger.log(
                    Level.SEVERE,
                    "An error occurred while enabling " + manifest.getName(),
                    exception);
            disableModule(manifest, logger);
        }
    }

    /**
     * Disables a module
     *
     * @param manifest The manifest of the module to disable
     */
    public static void disableModule(@NotNull ModuleManifest manifest, @NotNull Logger logger) {
        try {
            logger.info("Disabling module \"" + manifest.getName() + "\"");
            for (ModuleManifest manifest2 : manifests) {
                if (manifest2.getDependencies().contains(manifest.getName())
                        && manifest2.isEnabled()) {
                    disableModule(manifest2, logger);
                }
            }
            if (manifest.isEnabled()) {
                manifest.getInstance().onDisable();
            }
        } catch (Exception exception) {
            logger.log(
                    Level.SEVERE,
                    "An error ocurred while disabling " + manifest.getName(),
                    exception);
        }
        if (manifest.isEnabled()) {
            unloadClasses(manifest, logger);
        }
    }

    /**
     * Loads all classes from a jar file and initializes the main class
     *
     * @param manifest The manifest of the module
     */
    @SuppressWarnings("unchecked")
    public static void loadClasses(@NotNull ModuleManifest manifest) {
        URL[] urls;
        try {
            urls = new URL[] {new URL("jar:file:" + manifest.getFile() + "!/")};
        } catch (MalformedURLException exception) {
            ObsidianCloudNode.getLogger()
                    .log(
                            Level.SEVERE,
                            "An error occurred while loading classes of " + manifest.getName(),
                            exception);
            return;
        }
        try (JarFile jarFile = new JarFile(manifest.getFile().toFile())) {
            ModuleManifest.ModuleClassLoader classLoader =
                    new ModuleManifest.ModuleClassLoader(urls, ModuleLoader.class.getClassLoader());
            Enumeration<JarEntry> entries = jarFile.entries();

            // Load Classes
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
                String classFileName =
                        entry.getName().substring(0, entry.getName().lastIndexOf("."));
                String className = classFileName.replace("/", ".");
                try {
                    Class.forName(className);
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    try {
                        classLoader.loadClass(className);
                    } catch (ClassNotFoundException ignored2) {
                    }
                }
            }

            // Initialize Main Class
            Class<?> mainClass = classLoader.loadClass(manifest.getMainClass());
            if (!Module.class.isAssignableFrom(mainClass)) {
                throw new ClassCastException(mainClass.getName() + " does not extend Module");
            }
            Class<? extends Module> moduleClass = (Class<? extends Module>) mainClass;
            Constructor<? extends Module> constructor;
            try {
                constructor = moduleClass.getDeclaredConstructor();
            } catch (NoSuchMethodException ignored) {
                throw new NoSuchMethodError(
                        mainClass.getName() + " does not have a default constructor");
            }
            Module module = constructor.newInstance();
            Field instanceField = ModuleManifest.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(manifest, module);
            Field classLoaderField = ModuleManifest.class.getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            classLoaderField.set(manifest, classLoader);
            Field manifestField = Module.class.getDeclaredField("manifest");
            manifestField.setAccessible(true);
            manifestField.set(module, manifest);
        } catch (IOException
                | ClassNotFoundException
                | NoSuchFieldException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException exception) {
            ObsidianCloudNode.getLogger()
                    .log(
                            Level.SEVERE,
                            "An error occurred while loading classes of " + manifest.getName(),
                            exception);
        }
    }

    /**
     * Unloads a module
     *
     * @param manifest The manifest of the module to unload
     */
    public static void unloadClasses(@NotNull ModuleManifest manifest, @NotNull Logger logger) {
        try {
            manifest.getClassLoader().close();
            Field instanceField = ModuleManifest.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(manifest, null);
            Field classLoaderField = ModuleManifest.class.getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            classLoaderField.set(manifest, null);
        } catch (Throwable exception) {
            logger.log(
                    Level.SEVERE,
                    "An error occurred while unloading classes of " + manifest.getName(),
                    exception);
        }
    }
}
