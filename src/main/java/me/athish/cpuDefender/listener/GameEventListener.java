package me.athish.cpuDefender.listener;

import me.athish.cpuDefender.game.GameHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class GameEventListener implements Listener {

    private final GameHandler gameHandler;
    private final boolean test = true;

    public GameEventListener(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void onTNTIgnite(TNTPrimeEvent event) {
        if (test) event.setCancelled(true);
       Bukkit.broadcastMessage(ChatColor.YELLOW + "TNT Ignite event cancelled");
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD && event.getIgnitingBlock().getType() != Material.TNT) {
            event.setCancelled(true);
        }

        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING && event.getIgnitingBlock().getType() == Material.TNT) {
            Bukkit.broadcastMessage(ChatColor.RED + "Did you forget to disable the weather?");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Ghast) {
            Ghast ghast = (Ghast) event.getEntity();
            gameHandler.onGhastDeath(ghast, ghast.getKiller());
        }
    }
}