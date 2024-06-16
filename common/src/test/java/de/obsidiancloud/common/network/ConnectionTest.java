package de.obsidiancloud.common.network;

import de.obsidiancloud.common.network.packets.C2STestPacket;
import de.obsidiancloud.common.network.packets.S2CTestPacket;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConnectionTest {
    @Test
    public void testGetPacketListeners() {
        Connection connection = new Connection();

        C2STestListener c2sTestListener = new C2STestListener();
        connection.addPacketListener(c2sTestListener);
        Assertions.assertEquals(
                List.of(c2sTestListener), connection.getPacketListeners(C2STestPacket.class));

        S2CTestListener s2cTestListener = new S2CTestListener();
        connection.addPacketListener(s2cTestListener);
        Assertions.assertEquals(
                List.of(s2cTestListener), connection.getPacketListeners(S2CTestPacket.class));

        Assertions.assertEquals(
                List.of(c2sTestListener, s2cTestListener),
                connection.getPacketListeners(Packet.class));
    }

    private static class C2STestListener implements PacketListener<C2STestPacket> {
        @Override
        public void handle(C2STestPacket packet, Connection connection) {}
    }

    private static class S2CTestListener implements PacketListener<S2CTestPacket> {
        @Override
        public void handle(S2CTestPacket packet, Connection connection) {}
    }
}
