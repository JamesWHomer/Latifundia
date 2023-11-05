package net.uber.latifundia.commands;

import net.uber.latifundia.CityState;
import net.uber.latifundia.CityStateManager;
import net.uber.latifundia.Latifundia;
import net.uber.latifundia.PlayerStalker;
import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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

        if (args[0].equals("citystate")) {
            handleCityState(player, args);
        } else {
            switch (args[0].toLowerCase()) {
                case "claim":
                    handleClaim(player, args);
                    break;
                case "unclaim":
                    handleUnclaim(player, args);
                    break;
                case "info":
                    handleInfo(player, args);
                    break;
                default:
                    player.sendMessage("Unknown command. Use /" + label + " for help.");
            }
        }

        return true;
    }

    private void handleClaim(Player player, String[] args) {

        if (!cityStateManager.isCitizen(player)) {
            player.sendMessage("You are not a citizen of a citystate");
            return;
        }

        CityState cityState = cityStateManager.getCityState(player);

        Chunk chunk = player.getLocation().getChunk();

        boolean successful = cityState.claimChunk(chunk);

        this.playerStalker.updateChunk(new Point(chunk.getX(), chunk.getZ()));

        if (successful) {
            player.sendMessage("Chunk successfully claimed.");
        } else {
            player.sendMessage("Chunk could not be claimed.");
        }

    }

    private void handleUnclaim(Player player, String[] args) {

        if (!cityStateManager.isCitizen(player)) {
            player.sendMessage("You are not a citizen of a citystate");
            return;
        }

        CityState cityState = cityStateManager.getCityState(player);

        Chunk chunk = player.getLocation().getChunk();

        boolean successful = cityState.unclaimChunk(chunk);

        if (successful) {
            player.sendMessage("Chunk successfully unclaimed.");
        } else {
            player.sendMessage("Chunk could not be unclaimed.");
        }

        this.playerStalker.updateChunk(new Point(chunk.getX(), chunk.getZ()));

    }

    private void handleInfo(Player player, String[] args) {

        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(player.getWorld());

        Point playerChunk = new Point(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

        UUID ownerUUID = worldTree.queryClaim(playerChunk);

        if (ownerUUID == null) {
            player.sendMessage("Chunk is unclaimed.");
        } else {
            CityState cityState = cityStateManager.getCityState(ownerUUID);
            player.sendMessage("Owner: " + cityState.getName());
        }

    }

    private void handleCityState(Player player, String[] args) {
        // Implementation of list command

        if (cityStateManager.isCitizen(player)) {
            CityState cityState = cityStateManager.getCityState(player);
            player.sendMessage("Your City State: " + cityState.getName());
        } else {
            player.sendMessage("You are not a citizen of a city state.");
            //Just for test
            cityStateManager.createCityState("TestCity", player);
        }
    }
}
