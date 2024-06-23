package de.obsidiancloud.common.event.events;

import de.obsidiancloud.common.event.Event;
import de.obsidiancloud.common.OCServer;

public class ServerCreateEvent implements Event {

    // public float getMaxRam() // zum beispiel

    // public float getMinRam() // zum beispiel
    public OCServer.Type getType() {return null;} //als beispiel
    @Override
    public int getId() {
        return 0;
    }

}
