package at.jacob0225.datapackloaderplugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VersionGetter {

    private final JavaPlugin plugin;
    private final GenUtil genUtil;

    public void getVersion(final Consumer<String> consumer) {
        // Load version.yml from resources
        InputStream versionStream = plugin.getResource("version.yml");
        if (versionStream == null) {
            plugin.getLogger().warning("version.yml not found. Update check disabled.");
            return;
        }
        
        FileConfiguration versionConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(versionStream, StandardCharsets.UTF_8)
        );
        
        String projectId = versionConfig.getString("modrinth-project-id", "");
        
        if (projectId == null || projectId.trim().isEmpty()) {
            // No project ID configured, skip update check
            return;
        }
        
        if (genUtil.isFolia()) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, task -> fetchVersion(consumer, projectId));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> fetchVersion(consumer, projectId));
        }
    }

    private void fetchVersion(Consumer<String> consumer, String projectId) {
        try {
            // Modrinth API v2 - Get latest version
            String url = "https://api.modrinth.com/v2/project/" + projectId + "/version?featured=true";
            
            try (InputStream inputStream = new URI(url).toURL().openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                
                scanner.useDelimiter("\\A");
                if (scanner.hasNext()) {
                    String json = scanner.next();
                    // Parse JSON to get version_number from first array element
                    // Simple extraction: "version_number":"X.X.X"
                    String versionPattern = "\"version_number\":\"";
                    int startIndex = json.indexOf(versionPattern);
                    if (startIndex != -1) {
                        startIndex += versionPattern.length();
                        int endIndex = json.indexOf("\"", startIndex);
                        if (endIndex != -1) {
                            String latestVersion = json.substring(startIndex, endIndex);
                            consumer.accept(latestVersion);
                        }
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to check for updates: " + e.getMessage());
        } catch (URISyntaxException e) {
            plugin.getLogger().warning("Invalid Modrinth project ID: " + e.getMessage());
        }
    }
}