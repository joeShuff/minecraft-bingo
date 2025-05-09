package com.extremelyd1.listener;

import com.extremelyd1.bingo.map.BingoCardItemFactory;
import com.extremelyd1.game.Game;
import com.extremelyd1.game.team.Team;
import com.extremelyd1.util.InventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InteractListener implements Listener {

    /**
     * The game instance.
     */
    private final Game game;

    /**
     * The bingo card item factory instance to check whether an item is a bingo card.
     */
    private final BingoCardItemFactory bingoCardItemFactory;

    public InteractListener(Game game, BingoCardItemFactory bingoCardItemFactory) {
        this.game = game;
        this.bingoCardItemFactory = bingoCardItemFactory;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Boolean isOp = e.getPlayer().isOp();

        if ((game.getState().equals(Game.State.PRE_GAME) && !isOp) || game.getState().equals(Game.State.POST_GAME)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Boolean isOp = e.getPlayer().isOp();

        if ((game.getState().equals(Game.State.PRE_GAME) && !isOp) || game.getState().equals(Game.State.POST_GAME)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent e) {
        Boolean isOp = e.getPlayer().isOp();

        if ((game.getState().equals(Game.State.PRE_GAME) && !isOp) || game.getState().equals(Game.State.POST_GAME)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (game.getState().equals(Game.State.PRE_GAME) || game.getState().equals(Game.State.POST_GAME)) {
            e.setCancelled(true);
        }
    }

    public void onBlock(BlockEvent e) {
        if (!game.getState().equals(Game.State.IN_GAME)) {
            if (e instanceof Cancellable) {
                ((Cancellable) e).setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (game.getState().equals(Game.State.POST_GAME)) {
            // Check if we right-clicked a block
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block clickedBlock = e.getClickedBlock();
                if (clickedBlock != null) {
                    // Obtain the NSM level for the world
                    World world = clickedBlock.getWorld();
                    CraftWorld craftWorld = (CraftWorld) world;
                    ServerLevel level = craftWorld.getHandle();

                    // Check if there is a block entity at the location of the clicked block
                    BlockEntity blockEntity = level.getBlockEntity(new BlockPos(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()));
                    if (blockEntity != null) {
                        // If this block entity is a randomizable container, we can generate the loot table for it
                        if (blockEntity instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
                            // Get the NSM player and unpack the loot table into this block
                            Player player = e.getPlayer();
                            CraftPlayer craftPlayer = (CraftPlayer) player;

                            randomizableContainerBlockEntity.unpackLootTable(craftPlayer.getHandle());
                        }
                    }

                    // Get block state and check if it is a container
                    BlockState blockState = clickedBlock.getState();
                    if (blockState instanceof Container container) {
                        // Create copy of inventory
                        Inventory inventory = InventoryUtil.copyInventory(container.getInventory());
                        // Show the player the copied inventory
                        e.getPlayer().openInventory(inventory);
                    }
                }
            }

            e.setCancelled(true);
            return;
        }

        List<Action> allowedPreGameOpActions = Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK);
        if (allowedPreGameOpActions.contains(action) && game.getState().equals(Game.State.PRE_GAME)) {
            if (!e.getPlayer().isOp()) {
                e.setCancelled(true);
                return;
            }
        }

        if (!allowedPreGameOpActions.contains(action) && game.getState().equals(Game.State.PRE_GAME)) {
            e.setCancelled(true);
            return;
        }

        if (!action.equals(Action.RIGHT_CLICK_AIR)
                && !action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (e.getHand() == null || !e.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        if (!e.hasItem()) {
            return;
        }

        ItemStack itemStack = e.getItem();
        if (!bingoCardItemFactory.isBingoCard(itemStack)) {
            return;
        }

        Team team = game.getTeamManager().getTeamByPlayer(e.getPlayer());
        if (team == null || team.isSpectatorTeam()) {
            return;
        }

        game.getBingoCard().getBingoCardInventory().show(e.getPlayer());
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        // Disallow interaction with entities in pre-game
        if (game.getState().equals(Game.State.PRE_GAME)) {
            e.setCancelled(true);
            return;
        }

        if (game.getState().equals(Game.State.POST_GAME)) {
            Entity entity = e.getRightClicked();

            // If the player clicked an entity with an inventory
            // show them the inventory
            // TODO: replicate villager trade window, since they only allow a single
            //  player to interact with them
            if (entity.getType().equals(EntityType.VILLAGER)) {
                return;
            }

            // Only allow the following inventory holding entities as interaction targets
            if (entity.getType().equals(EntityType.CHEST_MINECART)
                    || entity.getType().equals(EntityType.FURNACE_MINECART)
                    || entity.getType().equals(EntityType.HOPPER_MINECART)) {
                InventoryHolder inventoryHolder = (InventoryHolder) entity;

                e.getPlayer().openInventory(inventoryHolder.getInventory());
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();

        if (game.getState().equals(Game.State.PRE_GAME) && player.isOp()) {
            return;
        }

        if (!game.getState().equals(Game.State.IN_GAME)) {
            e.setCancelled(true);
            return;
        }

        if (e.getClick().equals(ClickType.MIDDLE)) {
            ItemStack itemStack = e.getCurrentItem();
            if (bingoCardItemFactory.isBingoCard(itemStack)) {
                Team team = game.getTeamManager().getTeamByPlayer(player);
                if (team == null || team.isSpectatorTeam()) {
                    return;
                }

                game.getBingoCard().getBingoCardInventory().show((Player) e.getWhoClicked());
            }
        }

        if (game.getBingoCard().getBingoCardInventory().isBingoCard(e.getView().getTopInventory())) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        ItemStack currentItem = e.getCurrentItem();

        if (currentItem == null) {
            return;
        }

        InventoryType invType = e.getClickedInventory().getType();

        PlayerInventory playerInventory = e.getWhoClicked().getInventory();

        // In certain inventory types and while performing certain inventory actions,
        // we need to check whether we can actually perform the action.
        // Because if this can't be done, it will not consume the ingredients in the recipe.
        if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            // We are dealing with shift-click, now we need to check whether
            // there is space in the player inventory to insert this item
            if (!InventoryUtil.canShiftClickItem(
                    playerInventory.getStorageContents(),
                    currentItem
            )) {
                return;
            }
        }

        if (invType.equals(InventoryType.WORKBENCH)
                || invType.equals(InventoryType.CRAFTING)
                || invType.equals(InventoryType.SMITHING)
                || invType.equals(InventoryType.STONECUTTER)
                || invType.equals(InventoryType.MERCHANT)
        ) {
            if (e.getClick().equals(ClickType.NUMBER_KEY)) {
                // We are dealing with a move to specific hotbar position
                if (playerInventory.getStorageContents()[e.getHotbarButton()] != null) {
                    // The slot we are trying to move the result to is non-empty, so minecraft will not move the item
                    return;
                }
            }

            if (e.getAction().equals(InventoryAction.NOTHING)) {
                return;
            }
        }

        game.onMaterialCollected(player, currentItem.getType());
    }
}
