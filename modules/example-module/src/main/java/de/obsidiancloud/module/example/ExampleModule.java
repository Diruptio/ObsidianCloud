package de.obsidiancloud.module.example;

import de.obsidiancloud.node.module.Module;

@SuppressWarnings("unused")
public class ExampleModule extends Module {
    @Override
    public void onEnable() {
        getLogger().info("Hello, World!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye, World!");
    }
}
