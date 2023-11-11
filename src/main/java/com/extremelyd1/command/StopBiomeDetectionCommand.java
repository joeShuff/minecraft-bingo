package com.extremelyd1.command;

import com.extremelyd1.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopBiomeDetectionCommand implements CommandExecutor {

    /**
     * The game instance
     */
    private final Game game;

    public StopBiomeDetectionCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        if (game.getWorldManager().biomeDetectionRunning()) {
            game.getWorldManager().stopBiomeDetection();
            player.sendMessage(ChatColor.GREEN + "Biome detection stopped");
        } else {
            player.sendMessage(ChatColor.RED + "Biome detection not running");
        }

        return true;
    }
}
