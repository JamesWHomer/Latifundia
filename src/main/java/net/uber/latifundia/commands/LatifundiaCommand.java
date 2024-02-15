package net.uber.latifundia.commands;

import net.uber.latifundia.GeneralUtils;
import net.uber.latifundia.citystates.CityState;
import net.uber.latifundia.citystates.CityStateManager;
import net.uber.latifundia.Latifundia;
import net.uber.latifundia.PlayerStalker;
import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class LatifundiaCommand implements CommandExecutor {

    private PlayerStalker playerStalker;
    private CityStateManager cityStateManager;
    private WorldTreeManager worldTreeManager;

    public LatifundiaCommand(PlayerStalker playerStalker, CityStateManager cityStateManager, WorldTreeManager worldTreeManager) {
        this.playerStalker = playerStalker;
        this.cityStateManager = cityStateManager;
        this.worldTreeManager = worldTreeManager;
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

        String command = args[0].toLowerCase();
        switch (command) {
            case "citystate":
                handleCityState(player, args);
                break;
            case "claim":
                handleClaim(player, args);
                break;
            case "unclaim":
                handleUnclaim(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "create":
                if (args.length == 2) {
                    handleCreate(player, args);
                } else {
                    player.sendMessage("Incorrect usage of /" + label + " create. Expected 2 arguments.");
                }
                break;
            case "abandon":
                handleAbandon(player, args);
                break;
            case "leave":
                handleLeave(player, args);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "accept":
                handleAccept(player, args);
                break;
            default:
                player.sendMessage("Unknown command. Use /" + label + " for help.");
                return true;
        }

        return true;
    }

    private void handleInvite(Player player, String[] args) {

        if (!cityStateManager.isMemberOfCityState(player)) {
            player.sendMessage(GeneralUtils.colour("&cYou are not a citizen of a CityState!"));
            return;
        }

        CityState cityState = cityStateManager.getCityState(player);

        if (!cityState.canInvite(player)) {
            player.sendMessage(GeneralUtils.colour("&cYou are not high enough rank to invite players!"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(GeneralUtils.colour("&cPlease specify the player to invite."));
            return;
        }

        Player invited = Bukkit.getPlayerExact(args[1]);

        if (args.length != 2 || invited == null) {
            player.sendMessage(GeneralUtils.colour("&cPlease specify a valid and currently online player."));
            return;
        }

        if (cityStateManager.isMemberOfCityState(invited)) {
            player.sendMessage(GeneralUtils.colour("&cThe invited player is already a member of a CityState!"));
            return;
        }

        cityState.invitePlayer(player);

    }

    private void handleAccept(Player player, String[] args) {

        if (cityStateManager.isMemberOfCityState(player)) {
            player.sendMessage(GeneralUtils.colour("&cYou are already a member of a CityState"));
            return;
        }

        if (args.length != 2 || cityStateManager.doesCityExist(args[1])) {
            player.sendMessage(GeneralUtils.colour("&cYou need to specify the CityState you are attempting to join."));
            return;
        }

        if (!cityStateManager.doesCityExist(args[1])) {
            player.sendMessage(GeneralUtils.colour("&cYou cannot accept an invite to a CityState that does not exist."));
            return;
        }

        CityState cityState = cityStateManager.getCityState(args[1]);

        cityState.acceptInvite(player);

    }

    private void handleLeave(Player player, String[] args) {

        CityState cityState = cityStateManager.getCityState(player);

        if (cityState.getPopulation() != 1) {
            player.sendMessage(GeneralUtils.colour("&cYou cannot leave your CityState as you are the only member! To continue with this action use the command &e/lf abandon"));
            return;
        }

        if (args.length != 2 || !Objects.equals(args[1], "confirm")) {
            player.sendMessage(GeneralUtils.colour("&cAre you sure you wish to leave your CityState? This action cannot be undone! To confirm please type &e/lf leave confirm"));
            return;
        }

        cityState.removeMember(player.getUniqueId());

    }

    private void handleAbandon(Player player, String[] args) {

        CityState cityState = cityStateManager.getCityState(player);

        if (cityState.getPopulation() != 1) {
            player.sendMessage(GeneralUtils.colour("&cYou cannot abandon a CityState as there are other members in it!"));
            return;
        }

        if (args.length != 2 || !Objects.equals(args[1], "confirm")) {
            player.sendMessage(GeneralUtils.colour("&cAre you sure you wish to abandon your CityState? This action cannot be undone! To confirm please type &e/lf abandon confirm"));
            return;
        }

        UUID uuid = cityStateManager.getCityState(player).getCityStateUUID();

        cityStateManager.deleteCityState(uuid);

        player.sendMessage(GeneralUtils.colour("&aYour CityState has been successfully abandoned."));

    }

    private void handleCreate(Player player, String[] args) {

        if (cityStateManager.isMemberOfCityState(player)) {
            player.sendMessage(GeneralUtils.colour("&cYou are already a member of a citystate!"));
            return;
        }

        if (worldTreeManager.getChunkOwner(player.getLocation()) != null) {
            player.sendMessage(GeneralUtils.colour("&cThe chunk you are currently in is already owned by someone else!"));
            return;
        }

        CityState cityState = cityStateManager.createCityState(args[1], player);

        player.sendMessage("CityState Created!");

    }

    private void handleClaim(Player player, String[] args) {

        if (!cityStateManager.isMemberOfCityState(player)) {
            player.sendMessage(GeneralUtils.colour("&cYou are not a citizen of a CityState!"));
            return;
        }

        CityState cityState = cityStateManager.getCityState(player);

        Chunk chunk = player.getLocation().getChunk();

        boolean successful = cityState.claimChunk(chunk);

        this.playerStalker.updateChunk(new Point(chunk.getX(), chunk.getZ()));

        if (successful) {
            player.sendMessage(GeneralUtils.colour("&aChunk has been successfully claimed."));
        } else {
            player.sendMessage(GeneralUtils.colour("&cChunk could not be claimed."));
        }

    }

    private void handleUnclaim(Player player, String[] args) {

        if (!cityStateManager.isMemberOfCityState(player)) {
            player.sendMessage(GeneralUtils.colour("&cYou are not a citizen of a CityState and so cannot claim this chunk."));
            return;
        }

        CityState cityState = cityStateManager.getCityState(player);

        Chunk chunk = player.getLocation().getChunk();

        boolean successful = cityState.unclaimChunk(chunk);

        if (successful) {
            player.sendMessage(GeneralUtils.colour("&aChunk successfully unclaimed."));
        } else {
            player.sendMessage(GeneralUtils.colour("&cChunk could not be unclaimed."));
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

        if (cityStateManager.isMemberOfCityState(player)) {
            CityState cityState = cityStateManager.getCityState(player);
            player.sendMessage("Your City State: " + cityState.getName());
        } else {
            player.sendMessage("You are not a citizen of a city state.");
            //Just for test
            cityStateManager.createCityState("TestCity", player);
        }
    }
}
