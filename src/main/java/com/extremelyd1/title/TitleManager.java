package com.extremelyd1.title;

import com.extremelyd1.game.Game;
import com.extremelyd1.game.team.PlayerTeam;
import com.extremelyd1.game.team.Team;
import com.extremelyd1.game.winCondition.WinReason;
import com.extremelyd1.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Manager class to send titles to players.
 */
public class TitleManager {

    public TitleManager() {
    }

    /**
     * Send the start title to all players.
     */
    public void sendStartTitle() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(
                    Component
                            .text("BINGO")
                            .color(NamedTextColor.BLUE)
                            .decorate(TextDecoration.BOLD),
                    Component
                            .text("Game has started!"),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(1500), Duration.ofMillis(500))
            ));
        }
    }

    /**
     * Send the end title to all players based on the win reason.
     * @param winReason The win reason.
     */
    public void sendEndTitle(WinReason winReason) {
        Component title;
        Component subtitle = Component.empty();

        switch (winReason.getReason()) {
            case COMPLETE:
                PlayerTeam team = winReason.getTeam();

                title = Component
                        .text("BINGO")
                        .color(NamedTextColor.BLUE)
                        .decorate(TextDecoration.BOLD);
                subtitle = Component
                        .text(team.getName())
                        .color(team.getColor())
                        .append(Component
                                .text(" team has won the game!")
                                .color(NamedTextColor.WHITE)
                        );
                break;
            case RANDOM_TIE:
                title = Component
                        .text("Game has ended!");
                subtitle = Component
                        .text("It is a tie")
                        .color(NamedTextColor.BLUE);
                break;
            case NO_WINNER:
            default:
                title = Component
                        .text("Game has ended!");
                break;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(
                    title,
                    subtitle,
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
            ));
        }
    }

    public void sendPreGameActionBar(Game game) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String message = ChatColor.GOLD + "Use " + ChatColor.GREEN + "/join <team> " + ChatColor.GOLD + "to join a team";
            Team playerTeam = game.getTeamManager().getTeamByPlayer(p);

            if (playerTeam != null) {
                message = ChatColor.GOLD + "You are on " + playerTeam.getColor() + playerTeam.getName() + ChatColor.GOLD + " team!";
            }

            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    public void sendInGameTabBar(Game game) {
        String titleExtra = "";

        if (game.getConfig().isTimerEnabled()) {
            titleExtra += " - " + ChatColor.GREEN + TimeUtil.formatTimeLeft(game.getGameTimer().getTimeLeft());
        } else {
            titleExtra += " - " + ChatColor.GOLD + TimeUtil.formatTimeLeft(game.getGameTimer().getTimePlayed());
        }

        String title = ChatColor.BOLD.toString() + ChatColor.BLUE + "BINGO" + titleExtra;

        for (Player player: Bukkit.getOnlinePlayers()) {
            String footer = ChatColor.GREEN + "L" +
                    ChatColor.WHITE + "(" +
                    ChatColor.GOLD + player.getWorld().getLoadedChunks().length +
                    ChatColor.WHITE + ") " +
                    ChatColor.RED + "FL" +
                    ChatColor.WHITE + "(" +
                    ChatColor.GOLD + player.getWorld().getForceLoadedChunks().size() +
                    ChatColor.WHITE + ") "+
                    ChatColor.YELLOW + "E" +
                    ChatColor.WHITE + "(" +
                    ChatColor.YELLOW + player.getWorld().getEntities().size() +
                    ChatColor.WHITE + ")";

            player.setPlayerListHeaderFooter(
                    title,
                    footer
            );
        }
    }
}
