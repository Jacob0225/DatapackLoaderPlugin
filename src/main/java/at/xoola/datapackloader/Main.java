package at.xoola.datapackloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import at.xoola.datapackloader.dp.Applier;
import at.xoola.datapackloader.util.LanguageManager;
import at.xoola.datapackloader.util.LevelChanger;
import at.xoola.datapackloader.util.Messager;
import at.xoola.datapackloader.util.WorldsDeleter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Main extends JavaPlugin {

    private static final String separator = FileSystems.getDefault().getSeparator();
    private LanguageManager languageManager;
    private Messager messager;

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        languageManager = new LanguageManager(this, getLogger());
        messager = new Messager(getLogger(), this, languageManager);
        Configuration config = getConfig();
        if (config.getBoolean("disable-plugin")) {
            getLogger().info(languageManager.getMessage("plugin.disabled"));
            return;
        }

        // Use the plugins directory directly as the source for .zip files
        File datapacksFolder = getDataFolder().getParentFile();

        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get("server.properties")));
        } catch (IOException e) {
            throw new RuntimeException("IOException: Failed to load 'server.properties'!", e);
        }

        boolean importEvent;
        String levelName = properties.getProperty("level-name");
        String worldDatapacksPath = getServer().getWorldContainer() + separator + levelName + separator + "datapacks";
        Applier datapackApplier = new Applier(separator);
        try {
            importEvent = datapackApplier.applyDatapacks(datapacksFolder, worldDatapacksPath);
        } catch (IOException e) {
            throw new RuntimeException("IOException: Failed to apply datapacks!", e);
        }

        if (config.getBoolean("developer-mode") && !config.getBoolean("dev-mode-applied")) {
            try {
                new WorldsDeleter().deleteOldWorlds(
                        Objects.requireNonNull(this.getServer().getWorldContainer().listFiles()),
                        levelName);
            } catch (IOException e) {
                throw new RuntimeException("IOException: Failed to delete old worlds!", e);
            }

            try {
                new LevelChanger(getLogger(), languageManager).changeLevelName();
            } catch (IOException e) {
                throw new RuntimeException("IOException: Failed to change 'level-name' in 'server.properties'!", e);
            }

            config.set("dev-mode-applied", true);
        } else {
            config.set("dev-mode-applied", false);
        }

        saveConfig();
        if (importEvent) {
            getLogger().info(languageManager.getMessage("plugin.stopping-server"));
            getServer().shutdown();
        }
    }

    @Override
    public void onEnable() {
        // Intentionally empty - all work is done in onLoad() before worlds are prepared
    }

    public void reloadPlugin() {
        try {
            reloadConfig();
            languageManager.reload();
            messager = new Messager(getLogger(), this, languageManager);
            getLogger().info("Plugin reloaded successfully.");
        } catch (Exception e) {
            getLogger().severe("Error reloading plugin: " + e.getMessage());
            throw e;
        }
    }
}