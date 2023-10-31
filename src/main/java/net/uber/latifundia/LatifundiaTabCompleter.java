package net.uber.latifundia;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LatifundiaTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], getSubCommands(), new ArrayList<>());
        }
        return null;
    }

    private List<String> getSubCommands() {
        List<String> subCommands = new ArrayList<>();
        subCommands.add("claim");
        subCommands.add("unclaim");
        subCommands.add("info");
        subCommands.add("list");
        return subCommands;
    }
}
