package net.uber.latifundia.commands;

import net.uber.latifundia.citystates.CityState;
import net.uber.latifundia.citystates.CityStateManager;
import org.bukkit.Bukkit;
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

        List<String> subCommands = new ArrayList<>();

        if (args.length == 1) {

            subCommands.add("info");

            if (cityStateManager.isMemberOfCityState(player)) {
                CityState cityState = cityStateManager.getCityState(player);

                if (cityState.getPopulation() == 1) {
                    subCommands.add("abandon");
                } else {
                    subCommands.add("leave");
                }

                if (cityState.canClaim(player)) {
                    if (cityState.ownsChunk(player.getLocation().getChunk())) {
                        subCommands.add("unclaim");
                    }
                }

                if (cityState.canUnclaim(player)) {
                    if (!cityState.ownsChunk(player.getLocation().getChunk())) {
                        subCommands.add("claim");
                    }
                }

                if (cityState.canInvite(player)) {
                    subCommands.add("invite");
                    subCommands.add("promote");
                }

            } else {
                subCommands.add("create");
            }

        } else if (args.length == 2) {
            if (cityStateManager.isMemberOfCityState(player)) {

                CityState cityState = cityStateManager.getCityState(player);

                if (cityState.canInvite(player)) {

                    List<String> filtered = new ArrayList<>();

                    for (Player listed : Bukkit.getOnlinePlayers()) {
                        if (!cityStateManager.isMemberOfCityState(player)) {
                            filtered.add(listed.getName());
                        }
                    }

                    subCommands.addAll(filtered);

                }

            }
        }

        return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());

    }

}
