package de.obsidiancloud.node.local.template.purpur;

import de.obsidiancloud.node.Node;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.util.AikarsFlags;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.FileSystemUtils;

public class PurpurTemplate extends OCTemplate {
    private final Path templatesDirectory = Path.of("generated-templates").resolve("purpur");
    private final Logger logger = Node.getInstance().getLogger();
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
            if (Files.exists(buildDirectory)) return;

            download(buildDirectory);
            prepare(buildDirectory);

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
        String[] command = AikarsFlags.generate("java", "512M", "server.jar", "--nogui");

        // First run
        new ProcessBuilder(command).directory(directory.toFile()).start().waitFor();

        // Accept EULA
        Files.write(directory.resolve("eula.txt"), "eula=true".getBytes());

        // Second run
        Process process = new ProcessBuilder(command).directory(directory.toFile()).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (true) {
            if (reader.readLine().matches(".*Done.*For help, type \"help\"")) {
                process.getOutputStream().write("stop\n".getBytes());
                process.getOutputStream().flush();
                break;
            }
        }
        reader.close();
        process.waitFor();

        // Clean up
        FileSystemUtils.deleteRecursively(directory.resolve("logs"));
        FileSystemUtils.deleteRecursively(directory.resolve("world"));
        FileSystemUtils.deleteRecursively(directory.resolve("world_nether"));
        FileSystemUtils.deleteRecursively(directory.resolve("world_the_end"));
    }
}
