package com.extremelyd1.command;

import com.extremelyd1.game.Game;
import com.extremelyd1.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WinConditionCommand implements TabExecutor {

    /**
     * The game instance
     */
    private final Game game;

    public WinConditionCommand(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!CommandUtil.checkCommandSender(sender, true, true)) {
            return true;
        }

        if (!game.getState().equals(Game.State.PRE_GAME)) {
            sender.sendMessage(
                    ChatColor.DARK_RED + "Error: "
                            + ChatColor.WHITE + "Can only execute this command in pre-game"
            );

            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(
                    ChatColor.DARK_RED + "Error: "
                            + ChatColor.WHITE + "Please provide a win condition type: 'full', 'lines' or 'lockout'"
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("full")) {
            game.getWinConditionChecker().setFullCard();

            Bukkit.broadcastMessage(
                    Game.PREFIX + "Full bingo card has been " + ChatColor.GREEN + "enabled"
            );

            game.onPregameUpdate();

            return true;
        } else if (args[0].equalsIgnoreCase("lines")) {
            if (args.length < 2) {
                sender.sendMessage(
                        ChatColor.DARK_RED + "Error: "
                                + ChatColor.WHITE + "Please provide a number to indicate how many lines need to be completed"
                );

                return true;
            }

            int numLines;
            try {
                numLines = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(
                        ChatColor.DARK_RED + "Error: "
                                + ChatColor.WHITE + "Could not parse arguments, please provide an integer"
                );

                return true;
            }

            if (numLines < 1 || numLines > 10) {
                sender.sendMessage(
                        ChatColor.DARK_RED + "Error: "
                                + ChatColor.WHITE + "Number of lines to complete must be "
                                + ChatColor.BOLD + "between"
                                + ChatColor.RESET + ChatColor.YELLOW + " 0"
                                + ChatColor.WHITE + " and "
                                + ChatColor.YELLOW + "11"
                );

                return true;
            }

            game.getWinConditionChecker().setNumLinesToComplete(numLines);

            Bukkit.broadcastMessage(
                    Game.PREFIX + "Number of lines (rows, columns or diagonals) to achieve bingo has been set to "
                            + ChatColor.YELLOW + numLines
            );

            game.onPregameUpdate();

            return true;
        } else if (args[0].equalsIgnoreCase("lockout")) {
            int completionsToLock = 1;
            if (args.length > 1) {
                try {
                    completionsToLock = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(
                            ChatColor.DARK_RED + "Error: "
                                    + ChatColor.WHITE + "Could not parse arguments, please provide an integer"
                    );

                    return true;
                }
            }

            // Check whether the given value is within bounds
            if (completionsToLock < 1 || completionsToLock > 8) {
                sender.sendMessage(
                        ChatColor.DARK_RED + "Error: "
                                + ChatColor.WHITE + "Lockout completions must be "
                                + ChatColor.BOLD + "between"
                                + ChatColor.RESET + ChatColor.YELLOW + " 0"
                                + ChatColor.WHITE + " and "
                                + ChatColor.YELLOW + "9"
                );

                return true;
            }

            game.getWinConditionChecker().setCompletionsToLock(completionsToLock);

            String message = Game.PREFIX + "Lockout has been "
                    + ChatColor.GREEN + "enabled"
                    + ChatColor.WHITE + ", items will lock after "
                    + ChatColor.YELLOW + completionsToLock
                    + ChatColor.WHITE;

            if (completionsToLock == 1) {
                message += " team has collected them";
            } else {
                message += " teams have collected them";
            }

            Bukkit.broadcastMessage(message);

            game.onPregameUpdate();

            return true;
        }

        sender.sendMessage(
                ChatColor.DARK_RED + "Error: "
                        + ChatColor.WHITE + "Please provide a valid win condition type: 'full', 'lines' or 'lockout'"
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("full", "lines", "lockout");
        }

        if (args.length == 2) {
            if (args[0].equals("lines")) {
                return Arrays.asList("3");
            }

            if (args[0].equals("lockout")) {
                return Arrays.asList("3");
            }
        }

        return Collections.emptyList();
    }
}
