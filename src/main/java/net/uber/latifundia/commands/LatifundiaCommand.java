package net.uber.latifundia.commands;

import net.uber.latifundia.CityState;
import net.uber.latifundia.CityStateManager;
import net.uber.latifundia.Latifundia;
import net.uber.latifundia.PlayerStalker;
import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.sql.Time;
import java.time.Instant;
import java.util.UUID;

public class LatifundiaCommand implements CommandExecutor {

    private PlayerStalker playerStalker;
    private CityStateManager cityStateManager;

    public LatifundiaCommand(PlayerStalker playerStalker, CityStateManager cityStateManager) {
        this.playerStalker = playerStalker;
        this.cityStateManager = cityStateManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            return false; // Shows the usage message from the plugin.yml file
        }

        switch (args[0].toLowerCase()) {
            case "claim":
                // Handle claim command

                handleClaim(player, args);

                break;
            case "unclaim":
                // Handle unclaim command
                handleUnclaim(player, args);
                break;
            case "info":
                // Handle info command
                handleInfo(player, args);
                break;
            case "citystate":
                // Handle list command
                handleCityState(player, args);
                break;
            default:
                player.sendMessage("Unknown command. Use /" + label + " for help.");
        }
        return true;
    }

    private void handleClaim(Player player, String[] args) {

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        boolean successful = worldTree.insertClaim(playerChunk, player.getUniqueId());

        if (successful) {
            player.sendMessage("Chunk successfully claimed.");
        } else {
            player.sendMessage("Chunk could not be claimed.");
        }

        this.playerStalker.updateChunk(playerChunk);

    }

    private void handleUnclaim(Player player, String[] args) {

        //Still doesn't check if player actually owns the chunk, will be implemented later.

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        boolean successful = worldTree.removeClaim(playerChunk);

        if (successful) {
            player.sendMessage("Chunk successfully unclaimed.");
        } else {
            player.sendMessage("Chunk could not be unclaimed.");
        }

        this.playerStalker.updateChunk(playerChunk);

    }

    private void handleInfo(Player player, String[] args) {

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        UUID ownerUUID = worldTree.queryClaim(playerChunk);

        if (ownerUUID == null) {
            player.sendMessage("Chunk is unclaimed.");
        } else {
            String owner = Bukkit.getOfflinePlayer(ownerUUID).getName();
            player.sendMessage("Owner: " + owner);
        }

    }

    private void handleCityState(Player player, String[] args) {
        // Implementation of list command
        CityState cityState = cityStateManager.getCityState(player);
        player.sendMessage("Your City State: " + cityState.getName());
    }
}
