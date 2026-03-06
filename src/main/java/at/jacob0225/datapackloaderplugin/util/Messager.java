package at.jacob0225.datapackloaderplugin.util;

import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.jacob0225.datapackloaderplugin.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class Messager {

    private final Logger logger;
    private final Main main;
    private final LanguageManager languageManager;

    public Messager(Logger logger, Main main, LanguageManager languageManager) {
        this.logger = logger;
        this.main = main;
        this.languageManager = languageManager;
    }

    public void sendHelpMessage(CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder(languageManager.getMessage("command.help.documentation"))
                .color(ChatColor.GRAY)
                .append(languageManager.getMessage("command.help.url"))
                .color(ChatColor.GREEN);
        sendMessage(sender, builder.create());
    }

    public void sendInvalidMessage(CommandSender sender, String command) {
        String message = languageManager.getMessage("command.invalid.usage", "{command}", command);
        ComponentBuilder builder = new ComponentBuilder(message).color(ChatColor.RED);
        sendMessage(sender, builder.create());
    }

    public void sendOnlyConsoleMessage(CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder(languageManager.getMessage("command.only-console"))
                .color(ChatColor.RED);
        sendMessage(sender, builder.create());
    }

    public void sendOnlyPlayerMessage(CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder(languageManager.getMessage("command.only-player"))
                .color(ChatColor.RED);
        sendMessage(sender, builder.create());
    }

    public void sendZipMessage(CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder(languageManager.getMessage("command.zip-required"))
                .color(ChatColor.RED);
        sendMessage(sender, builder.create());
    }

    public void sendReloadSuccess(CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder(languageManager.getMessage("command.reload.success"))
                .color(ChatColor.GREEN);
        sendMessage(sender, builder.create());
    }

    public void sendReloadError(CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder(languageManager.getMessage("command.reload.error"))
                .color(ChatColor.RED);
        sendMessage(sender, builder.create());
    }

    private void sendMessage(CommandSender sender, net.md_5.bungee.api.chat.BaseComponent[] message) {
        if (sender instanceof Player) {
            sender.spigot().sendMessage(message);
            return;
        }

        infoLog(message);
    }

    private void infoLog(net.md_5.bungee.api.chat.BaseComponent[] message) {
        logger.info(new TextComponent(message).toLegacyText().replaceAll("§[0-9a-fA-FklmnoKLMNO]", ""));
    }
}
