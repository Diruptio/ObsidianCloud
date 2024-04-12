package de.obsidiancloud.common.addon;

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

public class AddonLoader {
    private static final List<AddonManifest> manifests = new ArrayList<>();

    public static @Nullable Addon getAddon(@NotNull String name) {
        for (AddonManifest manifest : manifests) {
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
    public static void enableAddons(@NotNull Path directory, @NotNull Logger logger)
            throws IOException {
        List<Path> files;
        try (Stream<Path> stream = Files.list(directory)) {
            files = stream.toList();
        }
        List<AddonManifest> manifests = new ArrayList<>();
        for (Path file : files) {
            if (!file.getFileName().toString().endsWith(".jar")) {
                continue;
            }
            try {
                manifests.add(loadManifest(file));
            } catch (Exception exception) {
                logger.throwing(AddonLoader.class.getName(), "enableAddons", exception);
            }
        }
        AddonLoader.manifests.addAll(manifests);

        manifests.removeIf(
                manifest -> {
                    if (manifest.getDependencies().contains(manifest.getName())) {
                        logger.warning(
                                "Addon \"" + manifest.getName() + "\" has a dependency to itself");
                        return true;
                    }
                    for (AddonManifest manifest2 : manifests) {
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

        for (AddonManifest manifest : sortAddons(manifests)) {
            AddonLoader.enableAddon(manifest, logger);
        }
    }

    /** Disables all addons */
    public static void disableAddons(@NotNull Logger logger) {
        List<AddonManifest> sorted = new ArrayList<>(manifests);
        Collections.reverse(sorted);
        for (AddonManifest manifest : sorted) {
            if (manifest.isEnabled()) disableAddon(manifest, logger);
        }
    }

    private static @NotNull List<AddonManifest> sortAddons(@NotNull List<AddonManifest> manifests) {
        List<AddonManifest> sorted = new ArrayList<>();
        for (AddonManifest manifest : manifests) {
            int priority = 0;
            for (int i = 0; i < sorted.size(); i++) {
                AddonManifest manifest2 = sorted.get(i);
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
    public static boolean enableAddon(@NotNull AddonManifest manifest, Logger logger) {
        try {
            logger.info("Enabling addon \"" + manifest.getName() + "\"");
            for (String dependency : manifest.getDependencies()) {
                if (getAddon(dependency) == null) {
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
                    AddonLoader.class.getName(),
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
    public static void disableAddon(@NotNull AddonManifest manifest, @NotNull Logger logger) {
        try {
            logger.info("Disabling addon \"" + manifest.getName() + "\"");
            for (AddonManifest manifest2 : manifests) {
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
                    AddonLoader.class.getName(),
                    "disableAddon",
                    new Exception(
                            "An error ocurred while disabling " + manifest.getName(), exception));
        }
    }

    /**
     * Loads the addon manifest from a jar file
     *
     * @param file The file to load
     * @throws IOException If an error occurs while loading the manifest
     * @return The addon manifest
     */
    public static AddonManifest loadManifest(@NotNull Path file) throws IOException {
        try (JarFile jarFile = new JarFile(file.toFile())) {
            JarEntry addonYml = jarFile.getJarEntry("addon.yml");
            if (addonYml == null) {
                throw new IOException("Could not find addon.yml in \"" + file.getFileName() + "\"");
            }
            Map<String, Object> yml = new Yaml().load(jarFile.getInputStream(addonYml));
            jarFile.close();
            if (yml == null) {
                throw new IOException("Could parse addon.yml in \"" + file.getFileName() + "\"");
            }
            if (!yml.containsKey("name") || !(yml.get("name") instanceof String name)) {
                throw new IOException(
                        "Could not find \"name\" in addon.yml in \"" + file.getFileName() + "\"");
            }
            if (!yml.containsKey("main") || !(yml.get("main") instanceof String main)) {
                throw new IOException(
                        "Could not find \"main\" in addon.yml in \"" + file.getFileName() + "\"");
            }
            if (!yml.containsKey("version") || !(yml.get("version") instanceof String version)) {
                throw new IOException(
                        "Could not find \"version\" in addon.yml in \""
                                + file.getFileName()
                                + "\"");
            }
            List<String> authors = new ArrayList<>();
            if (yml.containsKey("authors") && yml.get("authors") instanceof String[] array) {
                authors.addAll(Arrays.asList(array));
            }
            String description = null;
            if (yml.containsKey("description") && yml.get("description") instanceof String desc) {
                description = desc;
            }
            List<String> dependencies = new ArrayList<>();
            if (yml.containsKey("dependencies")
                    && yml.get("dependencies") instanceof String[] array) {
                dependencies.addAll(Arrays.asList(array));
            }
            List<String> softDependencies = new ArrayList<>();
            if (yml.containsKey("softdependencies")
                    && yml.get("softdependencies") instanceof String[] array) {
                dependencies.addAll(Arrays.asList(array));
            }
            return new AddonManifest(
                    file,
                    name,
                    main,
                    version,
                    authors,
                    description,
                    dependencies,
                    softDependencies);
        } catch (IOException exception) {
            throw new IOException(
                    "Could not load manifest of \"" + file.getFileName() + "\"", exception);
        }
    }

    /**
     * Loads all classes from a jar file and initializes the main class
     *
     * @param manifest The manifest of the addon
     * @throws IOException If an error occurs while loading the classes
     */
    @SuppressWarnings("unchecked")
    public static void loadClasses(@NotNull AddonManifest manifest)
            throws IOException,
                    NoSuchFieldException,
                    ClassNotFoundException,
                    IllegalAccessException,
                    InvocationTargetException,
                    InstantiationException {
        try (JarFile jarFile = new JarFile(manifest.getFile().toFile())) {
            URL[] urls = {new URL("jar:file:" + manifest.getFile() + "!/")};
            AddonClassLoader classLoader =
                    new AddonClassLoader(urls, AddonLoader.class.getClassLoader());
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
                Class<?> mainClass = Class.forName(manifest.getMain());
                if (mainClass.isAssignableFrom(Addon.class)) {
                    Class<? extends Addon> addonClass = (Class<? extends Addon>) mainClass;
                    Constructor<? extends Addon> constructor;
                    try {
                        constructor = addonClass.getDeclaredConstructor();
                    } catch (NoSuchMethodException ignored) {
                        throw new NoSuchMethodError(
                                "Main class of \""
                                        + manifest.getName()
                                        + "\" does not have a default constructor");
                    }
                    Addon addon = constructor.newInstance();
                    Field instanceField = AddonManifest.class.getDeclaredField("instance");
                    instanceField.setAccessible(true);
                    instanceField.set(manifest, addon);
                    Field classLoaderField = AddonManifest.class.getDeclaredField("classLoader");
                    classLoaderField.setAccessible(true);
                    classLoaderField.set(manifest, classLoader);
                    Field manifestField = Addon.class.getDeclaredField("manifest");
                    manifestField.setAccessible(true);
                    manifestField.set(addon, manifest);
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
    public static void unloadClasses(@NotNull AddonManifest manifest) throws Exception {
        try {
            manifest.getClassLoader().close();
            Field instanceField = AddonManifest.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(manifest, null);
            Field classLoaderField = AddonManifest.class.getDeclaredField("classLoader");
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
                    for (AddonManifest manifest : manifests) {
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
