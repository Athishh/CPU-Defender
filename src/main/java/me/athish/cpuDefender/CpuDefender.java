package me.athish.cpuDefender;

import me.athish.cpuDefender.command.CpuDefenderCommand;
import me.athish.cpuDefender.game.GameHandler;
import me.athish.cpuDefender.listener.GameEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.*;

public final class CpuDefender extends JavaPlugin implements Listener {

    private String arenaWorldName = "Arena";

    @Override
    public void onEnable() {
        Path serverWorldsFolder = Paths.get(Bukkit.getWorldContainer().getAbsolutePath());
        Path arenaWorldPath = serverWorldsFolder.resolve(arenaWorldName);

        if (Files.exists(arenaWorldPath)) {
            getLogger().info("Found Arena world: " + arenaWorldName);
            Bukkit.createWorld(new org.bukkit.WorldCreator(arenaWorldName));
            Bukkit.getWorld(arenaWorldName).setAutoSave(false);
            Bukkit.getWorld(arenaWorldName).setDifficulty(Difficulty.HARD);
        } else {
            getLogger().severe("Arena world not found!");
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);

        GameHandler gameHandler = new GameHandler(this, arenaWorldName);
        CpuDefenderCommand commandExecutor = new CpuDefenderCommand(gameHandler);
        this.getCommand("cpudefender").setExecutor(commandExecutor);
        getServer().getPluginManager().registerEvents(new GameEventListener(gameHandler), this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Bukkit.getWorld(arenaWorldName) != null) {
            event.getPlayer().teleport(Bukkit.getWorld(arenaWorldName).getSpawnLocation());
        } else {
            event.getPlayer().sendMessage("CpuDefender Arena not loaded");
        }
    }
}