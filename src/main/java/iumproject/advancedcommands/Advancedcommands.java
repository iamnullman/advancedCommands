package iumproject.advancedcommands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Advancedcommands extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private Map<String, ConfigurationSection> commandMap;

    @Override
    public void onEnable() {
        commandMap = new HashMap<>();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection commandsSection = config.getConfigurationSection("commands");
        if (commandsSection != null) {
            for (String key : commandsSection.getKeys(false)) {
                ConfigurationSection section = commandsSection.getConfigurationSection(key);
                commandMap.put(section.getString("name"), section);
            }
        }

    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        String[] message = event.getMessage().split(" ");
        String command = message[0].substring(1);
        Player player = event.getPlayer();
        ConfigurationSection section = commandMap.get(command);
        if (section != null) {
            String permissions = section.getString("permissions", "iumadvancedCommands");
            String sendMessage = section.getString("message");
            String permError = section.getString("error_message", "Bu komutu kullanmak için yetkiniz bulunmuyor.");
            List<String> commands = section.getStringList("commands");
            boolean useConsole = section.getBoolean("usedConsole", false);
            if (!player.hasPermission(permissions)) player.sendMessage(permError);
            else {
                if (message != null) player.sendMessage(sendMessage);
                commands.forEach(cmd -> {
                    cmd = cmd.replace("%player%", player.getName());
                    if (useConsole) {
                        getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
                    } else {
                        player.performCommand(cmd);
                    }
                });
            }
            }
        event.setCancelled(true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
