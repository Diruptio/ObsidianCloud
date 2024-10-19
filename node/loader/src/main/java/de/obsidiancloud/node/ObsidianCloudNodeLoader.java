package de.obsidiancloud.node;

import de.obsidiancloud.node.plugin.DefaultPluginLoader;
import de.obsidiancloud.node.plugin.PluginLoader;
import de.obsidiancloud.node.util.SharedClassLoader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.additionalclassprovider.MutableBasicClassProvider;
import net.lenni0451.classtransform.mixinstranslator.MixinsTranslator;
import net.lenni0451.classtransform.utils.FailStrategy;
import net.lenni0451.classtransform.utils.loader.InjectionClassLoader;
import net.lenni0451.classtransform.utils.tree.IClassProvider;

public class ObsidianCloudNodeLoader {
    private static final Logger logger = Logger.getLogger("loader");

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of("logs"));
            LogManager.getLogManager()
                    .readConfiguration(
                            ObsidianCloudNodeLoader.class.getClassLoader().getResourceAsStream("logging.properties"));
            logger.info("Loading the Node...");

            InputStream inputStream = ObsidianCloudNodeLoader.class.getResourceAsStream("/OCNode.jar");
            Path tempFile = Files.createTempFile("OCNode", ".jar");
            tempFile.toFile().deleteOnExit();
            Files.copy(Objects.requireNonNull(inputStream), tempFile, StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();

            IClassProvider classProvider = new MutableBasicClassProvider(new SharedClassLoader());
            TransformerManager transformerManager = new TransformerManager(classProvider);
            transformerManager.setFailStrategy(FailStrategy.CONTINUE);
            transformerManager.addTransformerPreprocessor(new MixinsTranslator());
            InjectionClassLoader nodeClassLoader = new InjectionClassLoader(
                    transformerManager,
                    new SharedClassLoader(),
                    tempFile.toUri().toURL());

            PluginLoader pluginLoader = new DefaultPluginLoader(transformerManager);
            pluginLoader.loadPlugins();

            Class<?> clazz = nodeClassLoader.loadClass("de.obsidiancloud.node.ObsidianCloudNode");
            Field field = clazz.getDeclaredField("pluginLoader");
            field.setAccessible(true);
            field.set(null, pluginLoader);

            logger.info("Launching the Node...");
            Method method = clazz.getDeclaredMethod("main", String[].class);
            method.invoke(null, (Object) args);
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "Failed to load the Node", exception);
        }
    }
}
