package at.jacob0225.datapackloaderplugin.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import at.jacob0225.datapackloaderplugin.Main;

public class LanguageManager {

    private final Main plugin;
    private final Logger logger;
    private FileConfiguration messages;
    private String currentLanguage;

    public LanguageManager(Main plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.currentLanguage = plugin.getConfig().getString("language", "en");
        loadLanguage();
    }

    private void loadLanguage() {
        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // Save default language files
        saveDefaultLanguageFiles();

        // Load selected language
        File langFile = new File(langFolder, "messages_" + currentLanguage + ".yml");
        
        if (!langFile.exists()) {
            logger.warning("Language file for '" + currentLanguage + "' not found. Using English.");
            currentLanguage = "en";
            langFile = new File(langFolder, "messages_en.yml");
        }

        messages = YamlConfiguration.loadConfiguration(langFile);
        
        // Load defaults from resources
        InputStream defaultStream = plugin.getResource("languages/messages_" + currentLanguage + ".yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
            );
            messages.setDefaults(defaultConfig);
        }
        
        logger.info("Loaded language: " + currentLanguage);
    }

    private void saveDefaultLanguageFiles() {
        saveResource("languages/messages_en.yml");
        saveResource("languages/messages_de.yml");
    }

    private void saveResource(String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }

    public String getMessage(String key) {
        String message = messages.getString(key);
        if (message == null) {
            logger.warning("Missing translation key: " + key);
            return "Missing translation: " + key;
        }
        return message;
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }

    public void reload() {
        this.currentLanguage = plugin.getConfig().getString("language", "en");
        loadLanguage();
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
