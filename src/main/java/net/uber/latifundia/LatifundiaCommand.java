package net.uber.latifundia;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            case "list":
                // Handle list command
                handleList(player, args);
                break;
            default:
                player.sendMessage("Unknown command. Use /" + label + " for help.");
        }
        return true;
    }

    private void handleClaim(Player player, String[] args) {
        // Implementation of claim command
        player.sendMessage("Claiming land...");
    }

    private void handleUnclaim(Player player, String[] args) {
        // Implementation of unclaim command
        player.sendMessage("Unclaiming land...");
    }

    private void handleInfo(Player player, String[] args) {
        // Implementation of info command
        player.sendMessage("Getting land information...");
    }

    private void handleList(Player player, String[] args) {
        // Implementation of list command
        player.sendMessage("Listing all claims...");
    }
}
