package de.obsidiancloud.node.local.template.fabric;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import de.obsidiancloud.node.ObsidianCloudNode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.FileSystemUtils;

public class FabricTemplate extends OCTemplate {
    private final Path templatesDirectory = Path.of("generated-templates").resolve("fabric");
    private final Logger logger = ObsidianCloudNode.getLogger();
    private final String version;
    private final String loader;
    private final String installer;

    public FabricTemplate(
            @NotNull String version, @NotNull String loader, @NotNull String installer) {
        super("fabric/%s/%s/%s".formatted(version, loader, installer));
        this.version = version;
        this.loader = loader;
        this.installer = installer();
    }

    @Override
    public void apply(@NotNull Path targetDirectory) {
        try {
            Path buildDirectory =
                    templatesDirectory.resolve(version).resolve(loader).resolve(installer);
            if (!Files.exists(buildDirectory)) {
                download(buildDirectory);
                prepare(buildDirectory);
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
            logger.log(Level.SEVERE, "Failed to compose template " + getPath(), exception);
        }
    }

    private void download(@NotNull Path directory) throws IOException {
        String url =
                "https://meta.fabricmc.net/v2/versions/loader/%s/%s/%s/server/jar"
                        .formatted(version, loader, installer);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        if (con.getResponseCode() != 200) return;
        Path file = directory.resolve("server.jar");
        Files.createDirectories(file.getParent());
        Files.createFile(file);
        con.getInputStream().transferTo(Files.newOutputStream(file));
    }

    private @NotNull String installer() {
        try {
            String url =
                    "https://meta.fabricmc.net/v2/versions/installer"
                            .formatted(version, loader, installer);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            if (con.getResponseCode() != 200) throw new RuntimeException();
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            JsonArray json = new JsonStreamParser(reader).next().getAsJsonArray();
            if (json.isEmpty()) throw new RuntimeException();

            for (int index = json.size() - 1; index > 0; index--) {
                final JsonObject jsonObject = json.get(index).getAsJsonObject();

                if (jsonObject.get("stable").getAsBoolean()) {
                    return jsonObject.get("version").getAsString();
                }
            }

            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
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
        new ProcessBuilder(command).directory(directory.toFile()).start().waitFor();

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
        FileSystemUtils.deleteRecursively(directory.resolve("logs"));
        FileSystemUtils.deleteRecursively(directory.resolve("world"));
        FileSystemUtils.deleteRecursively(directory.resolve("world_nether"));
        FileSystemUtils.deleteRecursively(directory.resolve("world_the_end"));
    }
}