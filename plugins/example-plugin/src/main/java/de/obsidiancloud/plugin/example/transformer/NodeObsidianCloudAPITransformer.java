package de.obsidiancloud.plugin.example.transformer;

import de.obsidiancloud.common.OCTask;
import de.obsidiancloud.node.NodeObsidianCloudAPI;
import de.obsidiancloud.plugin.example.ExamplePlugin;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CInline;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;

@CTransformer(NodeObsidianCloudAPI.class)
public class NodeObsidianCloudAPITransformer {
    @CInline
    @CInject(method = "createServer", target = @CTarget("HEAD"))
    public void createServer(OCTask task, InjectionCallback ic) {
        ExamplePlugin.getInstance()
                .getLogger()
                .info("ObsidianCloud now creates a Server from the Task \"" + task.name() + "\"");
    }
}
