package com.extremelyd1.command;

import com.extremelyd1.game.Game;
import com.extremelyd1.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public class StartCommand implements TabExecutor {

    /**
     * The game instance
     */
    private final Game game;

    public StartCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!CommandUtil.checkCommandSender(sender, true, true)) {
            return true;
        }

        if (game.getState().equals(Game.State.IN_GAME)) {
            sender.sendMessage(
                    ChatColor.DARK_RED + "Error: "
                            + ChatColor.WHITE + "cannot start game, it already has been started"
            );

            return true;
        }

        if (sender instanceof Player) {
            boolean allPlayersOnTeam = true;

            for (Player p: Bukkit.getOnlinePlayers()) {
                if (game.getTeamManager().getTeamByPlayer(p) == null) {
                    p.sendMessage(ChatColor.RED + "You need to select a team!");
                    allPlayersOnTeam = false;
                }
            }

            boolean confirmedCommand = args.length > 0 && args[0] == "confirm";

            if (!allPlayersOnTeam && !confirmedCommand) {
                sender.sendMessage(ChatColor.RED + "Not all players are on a team. Run /start confirm to continue regardless.");
            } else {
                game.start((Player) sender);
            }
        } else {
            game.start();
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("confirm");
        }

        return emptyList();
    }
}
