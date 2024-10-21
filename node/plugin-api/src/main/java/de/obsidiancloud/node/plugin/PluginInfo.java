package de.obsidiancloud.node.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginInfo {
    /**
     * The name of the plugin
     *
     * @return The name
     */
    @NotNull
    String name();

    /**
     * The dependencies of the plugin are names of other plugins that are required to be loaded before this plugin
     *
     * @return The dependencies
     */
    @NotNull
    String[] dependencies() default {};

    /**
     * The soft dependencies of the plugin are, like the normal dependencies, names of other plugins that are required
     * to be loaded before this plugin, bot only if they are available
     *
     * @return The soft dependencies
     */
    @NotNull
    String[] softDependencies() default {};
}
