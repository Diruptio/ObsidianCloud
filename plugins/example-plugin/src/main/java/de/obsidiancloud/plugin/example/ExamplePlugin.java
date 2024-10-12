package de.obsidiancloud.plugin.example;

import de.obsidiancloud.node.plugin.Plugin;
import de.obsidiancloud.node.plugin.PluginInfo;

@PluginInfo(name = "ExamplePlugin")
public class ExamplePlugin extends Plugin {
    @Override
    public void onEnable() {
        getLogger().info("ExamplePlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ExamplePlugin disabled!");
    }
}
