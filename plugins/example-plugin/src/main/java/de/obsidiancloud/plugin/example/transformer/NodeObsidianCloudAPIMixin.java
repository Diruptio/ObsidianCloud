package de.obsidiancloud.plugin.example.transformer;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.node.NodeObsidianCloudAPI;
import de.obsidiancloud.plugin.example.ExamplePlugin;
import net.lenni0451.classtransform.InjectionCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(NodeObsidianCloudAPI.class)
public class NodeObsidianCloudAPIMixin {
    @Inject(method = "deleteServer", at = @At("HEAD"))
    public void createServer(OCServer server, InjectionCallback ic) {
        ExamplePlugin.getInstance()
                .getLogger()
                .info("ObsidianCloud now deletes the Server \"" + server.getName() + "\"");
    }
}
