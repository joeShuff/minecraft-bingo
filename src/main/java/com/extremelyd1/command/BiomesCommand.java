package com.extremelyd1.command;

import com.extremelyd1.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class BiomesCommand implements TabExecutor {

    /**
     * The game instance
     */
    private final Game game;

    public BiomesCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        Player player = ((Player) commandSender);

        player.sendMessage(ChatColor.BLUE + "The following biomes have been found in this world:");
        for (Biome b: game.getWorldManager().getFoundBiomes()) {
            player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + b.name());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
