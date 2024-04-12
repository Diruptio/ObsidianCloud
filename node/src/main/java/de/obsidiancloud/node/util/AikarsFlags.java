package de.obsidiancloud.node.util;

public class AikarsFlags {
    public static String[] generate(
            String executable, String memory, String file, String... flags) {
        String[] array = new String[25 + flags.length];
        array[0] = executable;
        array[1] = "-Xms" + memory;
        array[2] = "-Xmx" + memory;
        array[3] = "-XX:+UseG1GC";
        array[4] = "-XX:+ParallelRefProcEnabled";
        array[5] = "-XX:MaxGCPauseMillis=200";
        array[6] = "-XX:+UnlockExperimentalVMOptions";
        array[7] = "-XX:+DisableExplicitGC";
        array[8] = "-XX:+AlwaysPreTouch";
        array[9] = "-XX:G1NewSizePercent=30";
        array[10] = "-XX:G1MaxNewSizePercent=40";
        array[11] = "-XX:G1HeapRegionSize=8M";
        array[12] = "-XX:G1ReservePercent=20";
        array[13] = "-XX:G1HeapWastePercent=5";
        array[14] = "-XX:G1MixedGCCountTarget=4";
        array[15] = "-XX:InitiatingHeapOccupancyPercent=15";
        array[16] = "-XX:G1MixedGCLiveThresholdPercent=90";
        array[17] = "-XX:G1RSetUpdatingPauseTimePercent=5";
        array[18] = "-XX:SurvivorRatio=32";
        array[19] = "-XX:+PerfDisableSharedMem";
        array[20] = "-XX:MaxTenuringThreshold=1";
        array[21] = "-Dusing.aikars.flags=https://mcflags.emc.gs";
        array[22] = "-Daikars.new.flags=true";
        array[23] = "-jar";
        array[24] = file;
        System.arraycopy(flags, 0, array, 25, flags.length);
        return array;
    }
}
