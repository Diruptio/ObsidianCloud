package de.obsidiancloud.node.local.template.purpur;

import de.obsidiancloud.node.ObsidianCloudNode;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.util.AikarsFlags;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

public class PurpurTemplate extends OCTemplate {
    private static final Path templatesDirectory = Path.of("generated-templates").resolve("purpur");
    private static final Logger logger = ObsidianCloudNode.getLogger();
    private static final Map<PurpurTemplate, Object> locks = new HashMap<>();
    private final String version;
    private final String build;

    public PurpurTemplate(@NotNull String version, @NotNull String build) {
        super("purpur/%s/%s".formatted(version, build));
        this.version = version;
        this.build = build;
    }

    @Override
    public void apply(@NotNull Path targetDirectory) {
        try {
            Path buildDirectory = templatesDirectory.resolve(version).resolve(build);

            if (locks.containsKey(this)) {
                locks.get(this).wait();
            } else if (!Files.exists(buildDirectory)) {
                locks.put(this, new Object());
                download(buildDirectory);
                prepare(buildDirectory);
                locks.remove(this).notifyAll();
            }

            try (Stream<Path> files = Files.list(buildDirectory)) {
                for (Path file : files.toList()) {
                    Files.copy(
                            file,
                            targetDirectory.resolve(file.getFileName()),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Throwable exception) {
            logger.log(Level.SEVERE, "Failed to apply template " + getPath(), exception);
        }
    }

    private void download(@NotNull Path directory) throws IOException {
        String url = "https://api.purpurmc.org/v2/purpur/%s/%s/download".formatted(version, build);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        if (con.getResponseCode() != 200) return;
        Path file = directory.resolve("server.jar");
        Files.createDirectories(file.getParent());
        Files.createFile(file);
        con.getInputStream().transferTo(Files.newOutputStream(file));
    }

    private void prepare(@NotNull Path directory) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-Xmx512M");
        command.add("-Xms512M");
        command.addAll(List.of(AikarsFlags.DEFAULT));
        command.add("-jar");
        command.add("server.jar");

        // First run
        new ProcessBuilder(command)
                .directory(directory.toFile())
                .start()
                .getInputStream()
                .transferTo(OutputStream.nullOutputStream());

        // Accept EULA
        Files.write(directory.resolve("eula.txt"), "eula=true".getBytes());

        // Second run
        Process process = new ProcessBuilder(command).directory(directory.toFile()).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (true) {
            if (reader.readLine().matches(".*Done.*For help, type \"help\".*")) {
                process.getOutputStream().write("stop\n".getBytes());
                process.getOutputStream().flush();
                break;
            }
        }
        reader.close();
        process.waitFor();

        // Clean up
        FileUtils.deleteDirectory(directory.resolve("logs").toFile());
        FileUtils.deleteDirectory(directory.resolve("world").toFile());
        FileUtils.deleteDirectory(directory.resolve("world_nether").toFile());
        FileUtils.deleteDirectory(directory.resolve("world_the_end").toFile());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PurpurTemplate other
                && version.equals(other.version)
                && build.equals(other.build);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, build);
    }
}
