package de.obsidiancloud.node.local.template.platform;

import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import org.jetbrains.annotations.NotNull;

public class PlatformTemplateProvider implements TemplateProvider {
    @Override
    public OCTemplate getTemplate(@NotNull String name) {
        if (name.equalsIgnoreCase("platform/paper")) {
            return new PaperPlatformTemplate(name);
        } else if (name.equalsIgnoreCase("platform/velocity")) {
            return new VelocityPlatformTemplate(name);
        } else {
            return null;
        }
    }
}
