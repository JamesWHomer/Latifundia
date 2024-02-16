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
import java.util.stream.Collectors;

public class LatifundiaTabCompleter implements TabCompleter {

    private CityStateManager cityStateManager;

    public LatifundiaTabCompleter(CityStateManager cityStateManager) {
        this.cityStateManager = cityStateManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        if (args.length == 0) return null;
        if (args.length == 1) return getFirstArgumentSuggestions(player, args[0]);
        if (args.length == 2) return getSecondArgumentSuggestions(player, args);

        return null;
    }

    private List<String> getFirstArgumentSuggestions(Player player, String currentArg) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("info");

        if (cityStateManager.isMemberOfCityState(player)) {
            CityState cityState = cityStateManager.getCityState(player);
            addCityStateSpecificSuggestions(player, cityState, suggestions);
        } else {
            suggestions.add("create");
        }

        return StringUtil.copyPartialMatches(currentArg, suggestions, new ArrayList<>());
    }

    private void addCityStateSpecificSuggestions(Player player, CityState cityState, List<String> suggestions) {
        if (cityState.getPopulation() == 1) {
            suggestions.add("abandon");
        } else {
            suggestions.add("leave");
        }

        if (cityState.canClaim(player)) {
            suggestions.add(cityState.ownsChunk(player.getLocation().getChunk()) ? "unclaim" : "claim");
        }

        if (cityState.canInvite(player)) {
            suggestions.addAll(List.of("invite", "promote", "kick"));
        }


    }

    private List<String> getSecondArgumentSuggestions(Player player, String[] args) {

        if (!cityStateManager.isMemberOfCityState(player)) return null;
        CityState cityState = cityStateManager.getCityState(player);

        if ("invite".equals(args[0]) && cityState.canInvite(player)) {
            return getOnlineSoloPlayers();
        }

        //Temporary method, make sure to fix canInvite or make a method idk or fix the damn system

        if ("promote".equals(args[0]) && cityState.canInvite(player)) {
            return getPromotableMembers(player, cityState);
        }

        if ("kick".equals(args[0]) && cityState.canInvite(player)) {
            return getKickableMembers(player, cityState);
        }

        return new ArrayList<>();

    }

    private List<String> getOnlineSoloPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !cityStateManager.isMemberOfCityState(player))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> getOnlineCityStatePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> cityStateManager.isMemberOfCityState(player))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> getPromotableMembers(Player player, CityState cityState) {
        List<String> list = new ArrayList<>();
        for (Player member : cityState.getOnlineMembers()) {
            if (!player.equals(member) && cityState.canPromote(player, member)) {
                list.add(member.getName());
            }
        }
        return list;
    }

    private List<String> getKickableMembers(Player player, CityState cityState) {
        List<String> list = new ArrayList<>();
        for (Player member : cityState.getOnlineMembers()) {
            if (!player.equals(member) && cityState.canKick(player, member)) {
                list.add(member.getName());
            }
        }
        return list;
    }

}
