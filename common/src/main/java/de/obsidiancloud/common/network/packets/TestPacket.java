package de.obsidiancloud.common.network.packets;

import de.obsidiancloud.common.network.Packet;
import io.netty.buffer.ByteBuf;
import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 02.06.2024
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TestPacket extends Packet {
    private String name;

    public TestPacket(@NotNull String targetConnectionId) {
        this.targetConnectionId = targetConnectionId;
    }

    @Override
    public void write(@NotNull ByteBuf byteBuf) {
        writeString(targetConnectionId, byteBuf);
        writeString(name, byteBuf);
    }

    @Override
    public void read(@NotNull ByteBuf byteBuf) {
        this.targetConnectionId = readString(byteBuf);
        this.name = readString(byteBuf);
    }
}
