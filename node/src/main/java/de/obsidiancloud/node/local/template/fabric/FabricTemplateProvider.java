package de.obsidiancloud.node.local.template.fabric;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import java.io.IOException;
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
                final JsonObject versionObject = version.getAsJsonObject();

                if (versionObject.get("stable").getAsBoolean()) {
                    versions.add(version.getAsJsonObject().get("version").getAsString());
                }
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
            version = versions.get(0);
        } else {
            version = path[1];
        }
        if (!versions.contains(version)) return null;

        String loader;
        if (path.length == 3) {
            loader = path[2];
        } else {
            loader = "latest";
        }
        loadLoaders(version);
        if (loaders.get(version).isEmpty()) return null;
        else if (loader.equalsIgnoreCase("latest")) {
            loader = loaders.get(version).get(0);
        } else if (!loaders.get(version).contains(loader)) return null;

        return new FabricTemplate(version, loader, installer());
    }

    private @NotNull String installer() {
        try {
            String url = "https://meta.fabricmc.net/v2/versions/installer";
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            if (con.getResponseCode() != 200) throw new RuntimeException();
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            JsonArray json = new JsonStreamParser(reader).next().getAsJsonArray();
            if (json.isEmpty()) throw new RuntimeException();

            for (final JsonElement jsonElement : json) {
                final JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.get("stable").getAsBoolean()) {
                    return jsonObject.get("version").getAsString();
                }
            }

            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void loadLoaders(@NotNull String version) {
        if (loaders.containsKey(version)) return;
        List<String> loaders = new ArrayList<>();
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
                    loaders.add(
                            String.valueOf(
                                    loader.getAsJsonObject()
                                            .get("loader")
                                            .getAsJsonObject()
                                            .get("version")
                                            .getAsString()));
                }
            }
        } catch (Throwable ignored) {
        }
        this.loaders.put(version, loaders);
    }
}
