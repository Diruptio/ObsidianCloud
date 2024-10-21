package de.obsidiancloud.plugin.example;

import de.obsidiancloud.node.plugin.Plugin;
import de.obsidiancloud.node.plugin.PluginInfo;

@PluginInfo(name = "ExamplePlugin")
public class ExamplePlugin extends Plugin {
    private static ExamplePlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("ExamplePlugin enabled!");
        applyClassTransformers("de.obsidiancloud.plugin.example.transformer");
    }

    @Override
    public void onDisable() {
        getLogger().info("ExamplePlugin disabled!");
    }

    public static ExamplePlugin getInstance() {
        return instance;
    }
}
