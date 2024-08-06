package de.obsidiancloud.common.event.events;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.event.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public record ServerCreateEvent(boolean connectToProxy) implements Event {
    // public float getMinRam() // zum beispiel
    // public float getMaxRam() // zum beispiel
    @Contract(pure = true)
    public OCServer.@Nullable Type getType() {
        return null;
    } // als beispiel

    @Override
    public int getId() {
        return 0;
    }
}
