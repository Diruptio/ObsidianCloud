package de.obsidiancloud.node.event.events;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.node.event.Event;

public class ServerCreateEvent implements Event {

    // public float getMaxRam() // zum beispiel

    // public float getMinRam() // zum beispiel
    public OCServer.Type getType() {
        return null;
    } // als beispiel

    @Override
    public int getId() {
        return 0;
    }
}
