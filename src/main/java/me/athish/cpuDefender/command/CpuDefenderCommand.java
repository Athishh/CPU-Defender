package me.athish.cpuDefender.command;

import me.athish.cpuDefender.game.GameHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CpuDefenderCommand implements CommandExecutor {

    private final GameHandler gameHandler;

    public CpuDefenderCommand(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /cpudefender <start|stop> [number of ghasts]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please specify the number of ghasts.");
                    return true;
                }
                int numberOfGhasts;
                try {
                    numberOfGhasts = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    numberOfGhasts = 1; // start game with 1 ghast
                }
                gameHandler.startGame(player, numberOfGhasts);
                break;
            case "stop":
                gameHandler.stopGame(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /cpudefender <start|stop> [number of ghasts]");
                break;
        }

        return true;
    }
}