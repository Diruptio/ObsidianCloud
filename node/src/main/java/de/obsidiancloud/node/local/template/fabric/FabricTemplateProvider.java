package de.obsidiancloud.node.local.template.fabric;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import de.obsidiancloud.node.local.template.paper.PaperTemplate;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class FabricTemplateProvider implements TemplateProvider {
    private final List<String> versions = new ArrayList<>();
    private final Map<String, List<String>> loaders = new HashMap<>();

    public FabricTemplateProvider() {
        try {
            String url = "https://meta.fabricmc.net/v2/versions/game";
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            if (con.getResponseCode() != 200) return;
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            JsonArray json = new JsonStreamParser(reader).next().getAsJsonArray();
            for (JsonElement version : json) {
                versions.add(version.getAsJsonObject().get("version").getAsString());
            }
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public OCTemplate getTemplate(@NotNull String name) {
        if (!name.startsWith("fabric")) return null;
        String[] path = name.split("/");
        if (path.length == 0) return null;

        String version;
        if (path.length == 1 || path[1].equalsIgnoreCase("latest")) {
            version = versions.get(versions.size() - 1);
        } else {
            version = path[1];
        }
        if (!versions.contains(version)) return null;

        String build;
        if (path.length == 3) {
            build = path[2];
        } else {
            build = "latest";
        }
        loadLoaders(version);
        if (loaders.get(version).isEmpty()) return null;
        else if (build.equalsIgnoreCase("latest")) {
            build = loaders.get(version).get(loaders.get(version).size() - 1);
        } else if (!loaders.get(version).contains(build)) return null;

        return new PaperTemplate(version, build);
    }

    private void loadLoaders(@NotNull String version) {
        if (loaders.containsKey(version)) return;
        List<String> builds = new ArrayList<>();
        String url = "https://meta.fabricmc.net/v2/versions/loader/%s".formatted(version);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if (con.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(con.getInputStream());
                JsonArray json = new JsonStreamParser(reader).next().getAsJsonArray();
                if (json.isEmpty()) return;
                for (JsonElement loader : json) {
                    builds.add(
                            String.valueOf(
                                    loader.getAsJsonObject()
                                            .get("loader")
                                            .getAsJsonObject()
                                            .get("version")
                                            .getAsInt()));
                }
            }
        } catch (Throwable ignored) {
        }
        this.loaders.put(version, builds);
    }
}
