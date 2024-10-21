package de.obsidiancloud.node.plugin;

import de.obsidiancloud.common.config.Config;
import de.obsidiancloud.node.util.SharedClassLoader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.lenni0451.classtransform.TransformerManager;
import org.jetbrains.annotations.NotNull;

public class DefaultPluginLoader implements PluginLoader {
    private final Logger logger = Logger.getLogger("DefaultPluginLoader");
    private final List<PluginInfo> pluginInfos = new ArrayList<>();
    private final Map<PluginInfo, SharedClassLoader> classLoaders = new HashMap<>();
    private final Map<PluginInfo, Class<?>> mainClasses = new HashMap<>();
    private final Map<PluginInfo, Plugin> loadedPlugins = new HashMap<>();
    private final TransformerManager transformerManager;

    public DefaultPluginLoader(@NotNull TransformerManager transformerManager) {
        this.transformerManager = transformerManager;
    }

    @Override
    public void loadPlugins() {
        logger.info("Loading plugins");
        try {
            Path pluginsDirectory = Path.of("plugins");
            Stream<Path> stream = Files.list(pluginsDirectory);
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".jar"))
                    .forEach(this::loadPluginInfo);
            stream.close();
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "An error occurred while loading plugins", exception);
        }
        for (PluginInfo info : pluginInfos) {
            loadPlugin(info, new Stack<>());
        }
        for (Plugin plugin : loadedPlugins.values()) {
            try {
                logger.info("Enabling plugin " + plugin.info.name());
                plugin.onEnable();
            } catch (Throwable exception) {
                logger.log(Level.SEVERE, "An error occurred while enabling plugin " + plugin.info.name(), exception);
                unloadPlugin(plugin.info, new Stack<>());
            }
        }
    }

    private boolean loadPlugin(@NotNull PluginInfo info, @NotNull Stack<PluginInfo> stack) {
        stack.push(info);
        for (String dependency : info.dependencies()) {
            PluginInfo dependencyInfo = null;
            for (PluginInfo pluginInfo : pluginInfos) {
                if (pluginInfo.name().equals(dependency)) {
                    dependencyInfo = pluginInfo;
                    break;
                }
            }
            if (dependencyInfo == null) {
                logger.log(
                        Level.SEVERE,
                        "Plugin \"" + info.name() + "\" cannot be loaded because the dependency \"" + dependency
                                + "\" is missing");
            } else if (stack.contains(dependencyInfo)) {
                logger.log(
                        Level.SEVERE,
                        "Plugin \"" + info.name() + "\" cannot be loaded because the dependency \"" + dependency
                                + "\" is a circular dependency");
                stack.remove(info);
                return false;
            } else if (!loadPlugin(dependencyInfo, stack)) {
                logger.log(
                        Level.SEVERE,
                        "Plugin \"" + info.name() + "\" cannot be loaded because the dependency \"" + dependency
                                + "\" could not be loaded");
                stack.remove(info);
                return false;
            }
        }
        for (String softDependency : info.softDependencies()) {
            PluginInfo softDependencyInfo = null;
            for (PluginInfo pluginInfo : pluginInfos) {
                if (pluginInfo.name().equals(softDependency)) {
                    softDependencyInfo = pluginInfo;
                    break;
                }
            }
            if (softDependencyInfo != null && stack.contains(softDependencyInfo)) {
                logger.log(
                        Level.SEVERE,
                        "Plugin \"" + info.name() + "\" cannot be loaded because the soft dependency \""
                                + softDependency + "\" is a circular dependency");
                stack.remove(info);
                return false;
            }
        }
        stack.remove(info);
        Class<?> mainClass = mainClasses.get(info);
        Constructor<?> constructor = null;
        for (Constructor<?> declaredConstructor : mainClass.getDeclaredConstructors()) {
            constructor = declaredConstructor;
            break;
        }
        if (constructor == null) {
            logger.log(
                    Level.SEVERE,
                    "Plugin \"" + info.name() + "\" cannot be loaded because \"" + mainClass.getName()
                            + "\" it does not have a default constructor");
            unloadPlugin(info, new Stack<>());
            return false;
        }
        try {
            logger.info("Loading plugin " + info.name());
            Plugin plugin = (Plugin) constructor.newInstance();
            plugin.info = info;
            plugin.loader = this;
            plugin.classLoader = classLoaders.get(info);
            plugin.transformerManager = transformerManager;
            plugin.config = new Config(Path.of("plugins", info.name(), "config.yml"), Config.Type.YAML);
            plugin.onLoad();
            loadedPlugins.put(info, plugin);
            return true;
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "An error occurred while loading plugin " + info.name(), exception);
            unloadPlugin(info, new Stack<>());
            return false;
        }
    }

    private void loadPluginInfo(@NotNull Path path) {
        try (JarFile jarFile = new JarFile(path.toFile())) {
            URL url = new URL("jar:file:" + path.toAbsolutePath() + "!/");
            SharedClassLoader classLoader = new SharedClassLoader(getClass().getClassLoader(), url);

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }
                String className = entry.getName()
                        .substring(0, entry.getName().length() - 6)
                        .replace('/', '.');
                Class<?> clazz = classLoader.loadClass(className);
                PluginInfo info = clazz.getAnnotation(PluginInfo.class);
                if (Plugin.class.isAssignableFrom(clazz) && info != null) {
                    pluginInfos.add(info);
                    classLoaders.put(info, classLoader);
                    mainClasses.put(info, clazz);
                }
            }
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "An error occurred while loading " + path, exception);
        }
    }

    @Override
    public void unloadPlugins() {
        logger.info("Unloading plugins");
        for (PluginInfo info : pluginInfos) {
            unloadPlugin(info, new Stack<>());
        }
    }

    private void unloadPlugin(@NotNull PluginInfo info, @NotNull Stack<PluginInfo> stack) {
        Plugin plugin = loadedPlugins.get(info);
        if (plugin != null) {
            stack.push(info);
            for (PluginInfo otherInfo : pluginInfos) {
                if (otherInfo != info && List.of(otherInfo.dependencies()).contains(info.name())) {
                    unloadPlugin(otherInfo, stack);
                } else if (otherInfo != info
                        && List.of(otherInfo.softDependencies()).contains(info.name())) {
                    unloadPlugin(otherInfo, stack);
                }
            }
            stack.remove(info);
            try {
                logger.info("Disabling plugin " + info.name());
                plugin.onDisable();
            } catch (Throwable exception) {
                logger.log(Level.SEVERE, "An error occurred while disabling plugin " + plugin.info.name(), exception);
            }
            logger.info("Unloading plugin " + info.name());
            loadedPlugins.remove(info);
        }
        mainClasses.remove(info);
        try {
            classLoaders.get(info).close();
        } catch (Throwable exception) {
            logger.log(
                    Level.SEVERE, "An error occurred while closing class loader for plugin " + info.name(), exception);
        }
    }

    public @NotNull Map<PluginInfo, Plugin> getLoadedPlugins() {
        return loadedPlugins;
    }

    public @NotNull Map<PluginInfo, SharedClassLoader> getClassLoaders() {
        return classLoaders;
    }
}
