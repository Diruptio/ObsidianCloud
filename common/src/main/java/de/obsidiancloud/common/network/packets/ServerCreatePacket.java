package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/** Creates a server with the given task. */
public class ServerCreatePacket extends Packet {
    private String task;

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(byteBuf, task);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        task = readString(byteBuf);
    }

    /**
     * Gets the task of the server
     *
     * @return The task of the server
     */
    public @NotNull String getTask() {
        return task;
    }

    /**
     * Sets the task of the server
     *
     * @param task The task of the server
     */
    public void setTask(@NotNull String task) {
        this.task = task;
    }
}
