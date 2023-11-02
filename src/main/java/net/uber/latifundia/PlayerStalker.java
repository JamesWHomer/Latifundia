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
    Map<UUID, UUID> lastNotifiedOwner = new HashMap<>();

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
        lastNotifiedOwner.remove(player.getUniqueId());

    }

    public boolean updatePlayer(Player player) {

        Point chunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        if (!playerChunkLocation.containsKey(player.getUniqueId())) {
            playerChunkLocation.put(player.getUniqueId(), chunk);
            lastNotifiedOwner.put(player.getUniqueId(), getChunkOwner(player.getWorld(), chunk));
            return false;
        }

        if (playerChunkLocation.get(player.getUniqueId()) == chunk) return false;

        // Passed checks and this means that the player is in a new chunk.

        UUID owner = getChunkOwner(player.getWorld(), chunk);

        playerChunkLocation.put(player.getUniqueId(), chunk);

        if (lastNotifiedOwner.get(player.getUniqueId()) == owner) {
            return false;
        }

        // Passed chunks and this means the player has not been notified of change yet.

        String ownerName;

        if (owner == null) {
            ownerName = "&cUnclaimed Lands";
        } else {
            ownerName = "&7" + Bukkit.getPlayer(owner).getName();
        }

        notifyPlayer(player, ownerName);

        lastNotifiedOwner.put(player.getUniqueId(), owner);

        return true;

    }

    public void updateChunk(Point chunk) {

        //Not finished yet.

        GeneralUtils.removeEntries(playerChunkLocation, chunk);

    }

    private UUID getChunkOwner(World world, Point chunk) {

        return worldTreeManager.getWorldTree(world).queryClaim(chunk);

    }

    private void notifyPlayer(Player player, String title) {

        String message = ChatColor.translateAlternateColorCodes('&', title);

        int fadeIn = 5;
        int stay = 15;
        int fadeOut = 10;
        player.sendTitle(message, "", fadeIn, stay, fadeOut);

    }

}
