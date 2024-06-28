package de.obsidiancloud.node.module;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

/**
 * The module loader loads and unloads modules
 */
public class ModuleLoader {
    private static final List<ModuleManifest> manifests = new ArrayList<>();

    public static @Nullable Module getModule(@NotNull String name) {
        for (ModuleManifest manifest : manifests) {
            if (manifest.getName().equals(name)) {
                return manifest.getInstance();
            }
        }
        return null;
    }

    /**
     * Loads all addons from a directory
     *
     * @param directory The directory to load
     * @throws IOException If an error occurs while loading the addons
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
            } catch (Exception exception) {
                logger.throwing(ModuleLoader.class.getName(), "enableAddons", exception);
            }
        }
        ModuleLoader.manifests.addAll(manifests);

        manifests.removeIf(
                manifest -> {
                    if (manifest.getDependencies().contains(manifest.getName())) {
                        logger.warning(
                                "Addon \"" + manifest.getName() + "\" has a dependency to itself");
                        return true;
                    }
                    for (ModuleManifest manifest2 : manifests) {
                        if (manifest.getDependencies().contains(manifest2.getName())
                                && manifest2.getDependencies().contains(manifest.getName())) {
                            logger.warning(
                                    "Addon \""
                                            + manifest.getName()
                                            + "\" and addon \""
                                            + manifest2.getName()
                                            + "\" have a dependency to each other");
                            return true;
                        }
                    }
                    return false;
                });

        for (ModuleManifest manifest : sortAddons(manifests)) {
            ModuleLoader.enableAddon(manifest, logger);
        }
    }

    /** Disables all addons */
    public static void disableAddons(@NotNull Logger logger) {
        List<ModuleManifest> sorted = new ArrayList<>(manifests);
        Collections.reverse(sorted);
        for (ModuleManifest manifest : sorted) {
            if (manifest.isEnabled()) disableAddon(manifest, logger);
        }
    }

    private static @NotNull List<ModuleManifest> sortAddons(@NotNull List<ModuleManifest> manifests) {
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
     * Enables an addon
     *
     * @param manifest The manifest of the addon to enable
     */
    public static boolean enableAddon(@NotNull ModuleManifest manifest, Logger logger) {
        try {
            logger.info("Enabling addon \"" + manifest.getName() + "\"");
            for (String dependency : manifest.getDependencies()) {
                if (getModule(dependency) == null) {
                    return false;
                }
            }
            if (!manifest.isEnabled()) {
                loadClasses(manifest);
            }
            manifest.getInstance().onEnable();
            return true;
        } catch (Exception exception) {
            logger.throwing(
                    ModuleLoader.class.getName(),
                    "enableAddon",
                    new Exception(
                            "An error ocurred while enabling " + manifest.getName(), exception));
            disableAddon(manifest, logger);
            return false;
        }
    }

    /**
     * Disables an addon
     *
     * @param manifest The manifest of the addon to disable
     */
    public static void disableAddon(@NotNull ModuleManifest manifest, @NotNull Logger logger) {
        try {
            logger.info("Disabling addon \"" + manifest.getName() + "\"");
            for (ModuleManifest manifest2 : manifests) {
                if (manifest2.getDependencies().contains(manifest.getName())
                        && manifest2.isEnabled()) {
                    disableAddon(manifest2, logger);
                }
            }
            if (manifest.isEnabled()) {
                manifest.getInstance().onDisable();
                unloadClasses(manifest);
            }
        } catch (Exception exception) {
            logger.throwing(
                    ModuleLoader.class.getName(),
                    "disableAddon",
                    new Exception(
                            "An error ocurred while disabling " + manifest.getName(), exception));
        }
    }

    /**
     * Loads all classes from a jar file and initializes the main class
     *
     * @param manifest The manifest of the addon
     * @throws IOException If an error occurs while loading the classes
     */
    @SuppressWarnings("unchecked")
    public static void loadClasses(@NotNull ModuleManifest manifest)
            throws IOException,
                    NoSuchFieldException,
                    ClassNotFoundException,
                    IllegalAccessException,
                    InvocationTargetException,
                    InstantiationException {
        try (JarFile jarFile = new JarFile(manifest.getFile().toFile())) {
            URL[] urls = {new URL("jar:file:" + manifest.getFile() + "!/")};
            AddonClassLoader classLoader =
                    new AddonClassLoader(urls, ModuleLoader.class.getClassLoader());
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }
                String className = entry.getName().substring(0, entry.getName().length() - 6);
                className = className.replace("/", ".");
                try {
                    Class.forName(className);
                } catch (ClassNotFoundException ignored) {
                    classLoader.loadClass(className);
                }
                jarFile.close();
                Class<?> mainClass = Class.forName(manifest.getMainClass());
                if (mainClass.isAssignableFrom(Module.class)) {
                    Class<? extends Module> addonClass = (Class<? extends Module>) mainClass;
                    Constructor<? extends Module> constructor;
                    try {
                        constructor = addonClass.getDeclaredConstructor();
                    } catch (NoSuchMethodException ignored) {
                        throw new NoSuchMethodError(
                                "Main class of \""
                                        + manifest.getName()
                                        + "\" does not have a default constructor");
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
                } else {
                    throw new ClassCastException(
                            "Main class of \"" + manifest.getName() + "\" does not extend Addon");
                }
            }
        }
    }

    /**
     * Unloads an addon
     *
     * @param manifest The manifest of the addon to unload
     * @throws Exception If an error occurs while unloading the addon
     */
    public static void unloadClasses(@NotNull ModuleManifest manifest) throws Exception {
        try {
            manifest.getClassLoader().close();
            Field instanceField = ModuleManifest.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(manifest, null);
            Field classLoaderField = ModuleManifest.class.getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            classLoaderField.set(manifest, null);
        } catch (Exception exception) {
            throw new Exception(
                    "Could not unload classes of \"" + manifest.getName() + "\"", exception);
        }
    }

    public static class AddonClassLoader extends URLClassLoader {
        public AddonClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return internalLoadClass(name, resolve, true);
        }

        private Class<?> internalLoadClass(String name, boolean resolve, boolean checkOther)
                throws ClassNotFoundException {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException exception) {
                if (checkOther) {
                    for (ModuleManifest manifest : manifests) {
                        try {
                            if (manifest.getClassLoader() != this) {
                                return manifest.getClassLoader()
                                        .internalLoadClass(name, resolve, false);
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
                throw exception;
            }
        }
    }
}
