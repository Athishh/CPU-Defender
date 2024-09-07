package me.athish.cpuDefender.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameHandler {

    private final JavaPlugin plugin;
    private final String copiedWorldName;
    private final Random random = new Random();
    private final Set<Ghast> spawnedGhasts = new HashSet<>();
    private BossBar bossBar;
    private BukkitRunnable musicRunnable;

    public GameHandler(JavaPlugin plugin, String worldname) {
        this.plugin = plugin;
        this.copiedWorldName = worldname;
    }

    public void startGame(Player player, int numberOfGhasts) {
        player.teleport(Bukkit.getWorld(copiedWorldName).getSpawnLocation());


        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK, 2);
        player.getInventory().addItem(sword);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SATURATION, 99999, 1));


        bossBar = Bukkit.createBossBar(ChatColor.RED + "Current Objective: SURVIVE", BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);


        for (int i = 0; i < numberOfGhasts; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 10 + random.nextDouble() * 15;
            Vector direction = new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance);
            direction.setY(15);

            Ghast ghast = (Ghast) player.getWorld().spawnEntity(player.getLocation().add(direction), EntityType.GHAST);
            ghast.setAI(true);
            ghast.setTarget(player);
            spawnedGhasts.add(ghast);
        }


        player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Game has Begun", ChatColor.RED + "May god have mercy", 10, 70, 20);


        playMusic(player);

        player.sendMessage(ChatColor.GREEN + "Goodluck Mate.");
    }

    public void stopGame(Player player) {

        for (Ghast ghast : spawnedGhasts) {
            if (ghast != null && !ghast.isDead()) {
                ghast.remove();
            }
        }
        spawnedGhasts.clear();

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }


        player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "VICTORY!", ChatColor.GREEN + "Your CPU lives!", 10, 70, 20);
        player.playSound(player.getLocation() ,Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);


        if (musicRunnable != null) {
            musicRunnable.cancel();
        }
        player.stopSound(Sound.MUSIC_DISC_PIGSTEP);


        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 10) {
                    this.cancel();
                    return;
                }

                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
                FireworkMeta meta = firework.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(getRandomColor())
                        .with(FireworkEffect.Type.BALL)
                        .withFlicker()
                        .withTrail()
                        .build();

                meta.addEffect(effect);
                meta.setPower(1);
                firework.setFireworkMeta(meta);

                // Set firework to explode at a random height between 5 and 10 blocks above the player
                firework.setVelocity(new Vector(0, 0.1 + random.nextDouble() * 0.1, 0));

                count++;
            }
        }.runTaskTimer(plugin, 0, 20);

        player.sendMessage(ChatColor.GREEN + "Game has ended, Your CPU lives to run another task!");
    }

    public void onGhastDeath(Ghast ghast, Player player) {
        spawnedGhasts.remove(ghast);
        if (bossBar != null) {
            bossBar.setProgress((double) spawnedGhasts.size() / (double) (spawnedGhasts.size() + 1));
        }
        if (spawnedGhasts.isEmpty()) {
            stopGame(player);
        }
    }

    private void playMusic(Player player) {
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 1.0f, 1.0f);

        musicRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 1.0f, 1.0f);
            }
        };
        musicRunnable.runTaskTimer(plugin, 0, 20 * (2 * 60 + 28)); // 2 minutes and 28 seconds in ticks for pigstep
    }

    private Color getRandomColor() {
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}