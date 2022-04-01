package com.extremelyd1.game.timer;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents the in game timer
 */
public class GameTimer {

    /**
     * The plugin instance
     */
    private final Plugin plugin;
    /**
     * How often the timer updates in seconds
     */
    private final long period;
    /**
     * The time left on this timer in seconds
     */
    private long timeLeft;

    /**
     * The time this game has been played
     */
    private long timePlayed;

    /**
     * A functional interface to execute every timer tick
     */
    private final TimerConsumer timerConsumer;

    /**
     * The timer runnable
     */
    private BukkitRunnable runnable;

    public GameTimer(Plugin plugin, long period, long time, TimerConsumer timerConsumer) {
        this.plugin = plugin;
        this.period = period;
        this.timeLeft = time;
        this.timePlayed = 0;
        this.timerConsumer = timerConsumer;
    }

    /**
     * Start this timer
     */
    public void start() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (timerConsumer.onTimer(timeLeft)) {
                    cancel();
                }

                timePlayed++;
                timeLeft--;
            }
        };

        runnable.runTaskTimer(
                plugin,
                0,
                period * 20
        );
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    /**
     * Cancel this timer
     */
    public void cancel() {
        if (runnable != null && !runnable.isCancelled()) {
            runnable.cancel();
        }
    }

}
