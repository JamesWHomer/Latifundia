package net.uber.latifundia.commands;

import net.uber.latifundia.Latifundia;
import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.sql.Time;
import java.time.Instant;

public class LatifundiaCommand implements CommandExecutor {

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

        long start = System.currentTimeMillis();

        // Implementation of claim command
        player.sendMessage("Claiming land...");

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        player.sendMessage("Claimed: " + worldTree.insertClaim(playerChunk, player.getUniqueId()));

        long end = System.currentTimeMillis();
        player.sendMessage("Time (ms): " + (end - start));

    }

    private void handleUnclaim(Player player, String[] args) {
        long start = System.currentTimeMillis();

        // Implementation of unclaim command
        player.sendMessage("Unclaiming land...");

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        player.sendMessage("Unclaimed: " + worldTree.removeClaim(playerChunk));

        long end = System.currentTimeMillis();
        player.sendMessage("Time (ms): " + (end - start));

    }

    private void handleInfo(Player player, String[] args) {
        long start = System.currentTimeMillis();
        // Implementation of info command

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        player.sendMessage("Getting land information...");
        player.sendMessage("Owner: " + worldTree.queryClaim(playerChunk));

        long end = System.currentTimeMillis();
        player.sendMessage("Time (ms): " + (end - start));
    }

    private void handleCityState(Player player, String[] args) {
        // Implementation of list command
        player.sendMessage("Handling citystate...");
    }
}