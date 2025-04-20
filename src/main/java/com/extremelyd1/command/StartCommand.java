package com.extremelyd1.command;

import com.extremelyd1.game.Game;
import com.extremelyd1.util.ChatUtil;
import com.extremelyd1.util.CommandUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;

@SuppressWarnings("UnstableApiUsage")
public class StartCommand implements BasicCommand {

    /**
     * The game instance.
     */
    private final Game game;

    public StartCommand(Game game) {
        this.game = game;
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!CommandUtil.checkCommandSender(commandSourceStack, true, true)) {
            return;
        }

        CommandSender sender = commandSourceStack.getSender();

        if (game.getState().equals(Game.State.IN_GAME)) {
            sender.sendMessage(ChatUtil.errorPrefix().append(Component
                    .text("Cannot start game, it has already been started")
                    .color(NamedTextColor.WHITE)
            ));

            return;
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
    }


    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("confirm");
        }

        return emptyList();
    }
}
