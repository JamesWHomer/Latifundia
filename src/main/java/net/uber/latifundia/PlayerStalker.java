package net.uber.latifundia;

import net.uber.latifundia.citystates.CityState;
import net.uber.latifundia.citystates.CityStateManager;
import net.uber.latifundia.citystates.Relation;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.*;

/**
 * This class handles player movements and interactions within different chunks
 * in a Minecraft world, notifying them about the ownership status of the chunks.
 */
public class PlayerStalker implements Listener {

    private final WorldTreeManager worldTreeManager;
    private final CityStateManager cityStateManager;

    // Maps each player's UUID to their last known chunk location
    private final Map<UUID, Point> playerChunkLocation = new HashMap<>();

    // Maps each player's UUID to the UUID of the last chunk's owner
    private final Map<UUID, UUID> lastChunksOwner = new HashMap<>();

    /**
     * Constructor for creating a PlayerStalker instance.
     *
     * @param worldTreeManager The manager for handling world trees.
     */
    public PlayerStalker(WorldTreeManager worldTreeManager, CityStateManager cityStateManager) {
        this.worldTreeManager = worldTreeManager;
        this.cityStateManager = cityStateManager;
    }

    public void removeOwner(UUID owner) {
        lastChunksOwner.values().removeIf(value -> value.equals(owner));
    }

    /**
     * Event handler for player movement.
     *
     * @param event The event triggered when a player moves.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        updatePlayer(event.getPlayer());
    }

    /**
     * Event handler for player joining the game.
     *
     * @param event The event triggered when a player joins the game.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer());
    }

    /**
     * Event handler for player quitting the game.
     *
     * @param event The event triggered when a player quits the game.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        playerChunkLocation.remove(playerUUID);
        lastChunksOwner.remove(playerUUID);
    }

    /**
     * Updates the chunk location and ownership status for a given player.
     *
     * @param player The player to be updated.
     * @return true if the chunk owner has changed, false otherwise.
     */
    public boolean updatePlayer(Player player) {
        Point chunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
        UUID playerUUID = player.getUniqueId();

        // Update the player's chunk location
        playerChunkLocation.put(playerUUID, chunk);

        // Get the owner of the current chunk
        UUID thisChunkOwner = getChunkOwner(player.getWorld(), chunk);

        // Check if the chunk owner has changed
        UUID lastChunkOwner = lastChunksOwner.get(playerUUID);
        if (!Objects.equals(thisChunkOwner, lastChunkOwner)) {
            // The chunk owner is different (or the player is entering a chunk for the first time)
            lastChunksOwner.put(playerUUID, thisChunkOwner);
            notifyPlayer(player, thisChunkOwner);
            // Change GameMode of Player
            updateGameMode(player, thisChunkOwner);
            return true;
        }

        // The chunk owner has not changed
        return false;
    }

    /**
     * Updates the Game Mode of the player.
     * @param player The player to be updated.
     * @param chunkOwner The owner of the chunk that the player is in
     */
    public void updateGameMode(Player player, UUID chunkOwner) {

        //Work on progress

        if ((player.getGameMode() == GameMode.SPECTATOR) || (player.getGameMode() == GameMode.CREATIVE)) {
            return;
        }

        if (chunkOwner == null) {
            //The Wild
            player.setGameMode(GameMode.SURVIVAL);
        } else {

            CityState playerCityState = cityStateManager.getCityState(player);

            if (playerCityState.getCityStateUUID() == chunkOwner) {
                player.setGameMode(GameMode.SURVIVAL);
                return;
            }

            Relation relation = playerCityState.getRelation(chunkOwner);

            switch (relation) {
                case NEUTRAL:
                case UNFRIENDLY:
                case FRIENDLY:
                    player.setGameMode(GameMode.ADVENTURE);
                    break;
                case ALLY:
                case WAR:
                    player.setGameMode(GameMode.SURVIVAL);
                    break;
            }

        }

    }

    /**
     * Updates the ownership status for all players in a given chunk.
     *
     * @param chunk The chunk to be updated.
     */
    public void updateChunk(Point chunk) {
        // Collect UUIDs of players in the chunk and remove their entries
        Set<UUID> matchingUUIDs = new HashSet<>();
        playerChunkLocation.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(chunk)) {
                matchingUUIDs.add(entry.getKey());
                return true;
            }
            return false;
        });

        // Update each player in the chunk
        for (UUID pUUID : matchingUUIDs) {
            Player player = Bukkit.getPlayer(pUUID);
            if (player != null) {
                updatePlayer(player);
            }
        }
    }

    /**
     * Retrieves the UUID of the owner of a chunk in a specific world.
     *
     * @param world The world containing the chunk.
     * @param chunk The chunk whose owner is to be retrieved.
     * @return The UUID of the owner of the chunk, or null if unclaimed.
     */
    private UUID getChunkOwner(World world, Point chunk) {
        return worldTreeManager.getWorldTree(world).queryClaim(chunk);
    }

    /**
     * Sends a notification to a player about the ownership status of their current chunk.
     *
     * @param player The player to notify.
     * @param owner The UUID of the owner of the chunk, or null if unclaimed.
     */
    private void notifyPlayer(Player player, UUID owner) {
        String ownerName = (owner == null) ? "&cUnclaimed Lands" : "&7" + cityStateManager.getCityState(owner).getName();
        sendTitle(player, ownerName);
    }

    /**
     * Sends a title message to a player.
     *
     * @param player The player to send the title to.
     * @param title The title message.
     */
    private void sendTitle(Player player, String title) {
        String message = ChatColor.translateAlternateColorCodes('&', title);
        int fadeIn = 5;
        int stay = 20;
        int fadeOut = 10;
        player.sendTitle(message, "", fadeIn, stay, fadeOut);
    }
}

