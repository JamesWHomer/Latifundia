package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.*;

public class PlayerStalker implements Listener {

    private WorldTreeManager worldTreeManager;

    // The last known chunk of the player
    Map<UUID, Point> playerChunkLocation = new HashMap<>();
    // To make sure that there isn't a notification every time the player changes chunks
    Map<UUID, UUID> lastChunksOwner = new HashMap<>();

    public PlayerStalker(WorldTreeManager worldTreeManager) {

        this.worldTreeManager = worldTreeManager;

    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        updatePlayer(player);

    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        updatePlayer(player);

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        playerChunkLocation.remove(player.getUniqueId());
        lastChunksOwner.remove(player.getUniqueId());

    }

    public boolean updatePlayer(Player player) {
        Point chunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
        UUID playerUUID = player.getUniqueId();

        // Update the player's chunk location
        playerChunkLocation.put(playerUUID, chunk);

        // Get the owner of the current chunk
        UUID thisChunkOwner = getChunkOwner(player.getWorld(), chunk);

        // Check if the chunk owner has changed
        UUID lastChunkOwner = lastChunksOwner.get(playerUUID);
        if ((thisChunkOwner == null ? lastChunkOwner != null : !thisChunkOwner.equals(lastChunkOwner))) {
            // The chunk owner is different (or the player is entering a chunk for the first time)
            lastChunksOwner.put(playerUUID, thisChunkOwner);
            notifyPlayer(player, thisChunkOwner);
            return true;
        }

        // The chunk owner has not changed
        return false;
    }


    public void updateChunk(Point chunk) {

        // Collect UUIDs and remove entries
        Set<UUID> matchingUUIDs = new HashSet<>();
        Iterator<Map.Entry<UUID, Point>> iterator = playerChunkLocation.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Point> entry = iterator.next();
            if (entry.getValue().equals(chunk)) {
                matchingUUIDs.add(entry.getKey());
                iterator.remove();
            }
        }

        for (UUID pUUID : matchingUUIDs) {

            Player player = Bukkit.getPlayer(pUUID);

            if (player == null) return;

            updatePlayer(player);

        }

    }

    private UUID getChunkOwner(World world, Point chunk) {

        return worldTreeManager.getWorldTree(world).queryClaim(chunk);

    }

    private void notifyPlayer(Player player, UUID owner) {

        String ownerName;

        if (owner == null) {
            ownerName = "&cUnclaimed Lands";
        } else {
            ownerName = "&7" + Bukkit.getPlayer(owner).getName();
        }

        sendTitle(player, ownerName);

    }

    private void sendTitle(Player player, String title) {

        String message = ChatColor.translateAlternateColorCodes('&', title);

        int fadeIn = 5;
        int stay = 15;
        int fadeOut = 10;
        player.sendTitle(message, "", fadeIn, stay, fadeOut);

    }

}
