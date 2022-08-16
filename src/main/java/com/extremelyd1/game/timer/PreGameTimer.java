package com.extremelyd1.game.timer;

import com.extremelyd1.game.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGameTimer extends BukkitRunnable {

    private Game game;

    public PreGameTimer(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        game.getTitleManager().sendPreGameActionBar(game);
    }
}
