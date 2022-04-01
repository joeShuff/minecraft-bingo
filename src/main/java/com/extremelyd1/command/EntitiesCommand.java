package com.extremelyd1.command;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EntitiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        World world = ((Player) sender).getWorld();

        sender.sendMessage("There are " + world.getEntities().size() + " entities");

        Map<String, Integer> entityCounts = new HashMap<>();

        for (Entity e: world.getEntities()) {
            String key = e.getType().toString();
            int amount = 1;

            if (entityCounts.containsKey(key)) {
                amount = entityCounts.get(key) + 1;
            }

            entityCounts.put(key, amount);
        }

        for (String key: entityCounts.keySet()) {
            sender.sendMessage(key + " x" + entityCounts.get(key));
        }

        return true;
    }
}