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
            ArrayList<Player> playersNotOnATeam = new ArrayList<Player>();

            for (Player p: Bukkit.getOnlinePlayers()) {
                if (game.getTeamManager().getTeamByPlayer(p) == null) {
                    p.sendMessage(ChatColor.DARK_RED + "WARNING" +
                            ChatColor.WHITE + ": " +
                            ChatColor.RED + "An admin tried to start the game but you're not on a team. Please join a team if you wish to participate.");
                    playersNotOnATeam.add(p);
                }
            }

            boolean allPlayersOnTeam = playersNotOnATeam.isEmpty();
            boolean confirmedCommand = args.length > 0 && args[0] == "confirm";

            if (!allPlayersOnTeam && !confirmedCommand) {
                String playerListMessage = "";

                for (Player p: playersNotOnATeam) {
                    playerListMessage += ChatColor.WHITE + "- " + ChatColor.RED + p.getDisplayName() + "\n";
                }

                sender.sendMessage(ChatColor.RED + "The following players aren't on a team \n" + playerListMessage + ChatColor.RED + "\nRun " + ChatColor.WHITE + "/start confirm" + ChatColor.RED + " to continue regardless.");
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
