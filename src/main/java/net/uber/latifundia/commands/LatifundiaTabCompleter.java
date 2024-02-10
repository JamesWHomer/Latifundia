package net.uber.latifundia.commands;

import net.uber.latifundia.citystates.CityState;
import net.uber.latifundia.citystates.CityStateManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LatifundiaTabCompleter implements TabCompleter {

    private CityStateManager cityStateManager;

    public LatifundiaTabCompleter(CityStateManager cityStateManager) {
        this.cityStateManager = cityStateManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        if (args.length == 0) return null;

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("claim");
            subCommands.add("unclaim");
            subCommands.add("info");

            if (cityStateManager.isMemberOfCityState(player)) {
                CityState cityState = cityStateManager.getCityState(player);

                if (cityState.getPopulation() == 1) {
                    subCommands.add("delete");
                } else {
                    subCommands.add("leave");
                }
            } else {
                subCommands.add("create");
            }

            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());

        }

        return null;
    }

    /*
    private List<String> getSubCommands() {
        List<String> subCommands = new ArrayList<>();
        subCommands.add("claim");
        subCommands.add("unclaim");
        subCommands.add("info");
        subCommands.add("citystate");
        subCommands.add("create");
        return subCommands;
    }
     */
}
