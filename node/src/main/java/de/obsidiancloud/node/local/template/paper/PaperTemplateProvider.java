package de.obsidiancloud.node.local.template.paper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import de.obsidiancloud.node.local.template.OCTemplate;
import de.obsidiancloud.node.local.template.TemplateProvider;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class PaperTemplateProvider implements TemplateProvider {
    private final List<String> versions = new ArrayList<>();
    private final Map<String, List<String>> builds = new HashMap<>();

    public PaperTemplateProvider() {
        try {
            String url = "https://papermc.io/api/v2/projects/paper";
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            if (con.getResponseCode() != 200) return;
            InputStreamReader reader = new InputStreamReader(con.getInputStream());
            JsonObject json = new JsonStreamParser(reader).next().getAsJsonObject();
            for (JsonElement version : json.get("versions").getAsJsonArray()) {
                versions.add(version.getAsString());
            }
        } catch (Throwable exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public OCTemplate getTemplate(@NotNull String name) {
        if (!name.startsWith("paper")) return null;
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
        loadBuilds(version);
        if (builds.get(version).isEmpty()) return null;
        else if (build.equalsIgnoreCase("latest")) {
            build = builds.get(version).get(builds.get(version).size() - 1);
        } else if (!builds.get(version).contains(build)) return null;

        return new PaperTemplate(version, build);
    }

    private void loadBuilds(@NotNull String version) {
        if (builds.containsKey(version)) return;
        List<String> builds = new ArrayList<>();
        String url =
                "https://api.papermc.io/v2/projects/paper/versions/%s/builds".formatted(version);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if (con.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(con.getInputStream());
                JsonObject json = new JsonStreamParser(reader).next().getAsJsonObject();
                JsonArray buildsArray = json.getAsJsonArray("builds");
                if (buildsArray.isEmpty()) return;
                for (JsonElement build : buildsArray) {
                    builds.add(String.valueOf(build.getAsJsonObject().get("build").getAsInt()));
                }
            }
        } catch (Throwable ignored) {
        }
        this.builds.put(version, builds);
    }
}
