package net.uber.latifundia.citystates;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.uber.latifundia.GeneralUtils;
import net.uber.latifundia.Latifundia;
import net.uber.latifundia.claimmanagement.WorldTree;
import net.uber.latifundia.claimmanagement.WorldTreeManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class CityState implements Serializable {
    private static final long serialVersionUID = 1L;

    // Planning on implementing Empires but not yet.

    private final String name;
    private final UUID cityStateUUID;
    private final Map<UUID, Rank> memberList = new HashMap<>();
    private final Map<String, List<Point>> worldClaimMap = new HashMap<>();
    private final Map<UUID, Relation> relationMap = new HashMap<>();
    private final Map<UUID, Long> invitedPlayers = new HashMap<>();

    /**
     * Creates new city state from scratch.
     * @param name Creates name of CityState
     * @param creator Clarifies who is Leader
     */
    public CityState(UUID cuuid, String name, Player creator) {
        this.name = name;
        this.cityStateUUID = cuuid;
        List<Point> claimList = new ArrayList<>();
        this.worldClaimMap.put(creator.getWorld().getName(), claimList);
        this.memberList.put(creator.getUniqueId(), Rank.LEADER);
    }

    public String getName() {
        return this.name;
    }

    public int getPopulation() {
        return memberList.size();
    }

    public Rank getRank(Player player) {
        return this.memberList.get(player.getUniqueId());
    }

    public Set<UUID> getMembers() {
        return memberList.keySet();
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<>();
        for (UUID uuid : getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                onlineMembers.add(player);
            }
        }
        return onlineMembers;
    }

    public UUID getCityStateUUID() {
        return this.cityStateUUID;
    }

    public void removeMember(Player player) {
        memberList.remove(player.getUniqueId());
        Latifundia.getPlugin(Latifundia.class).getCityStateManager().removeMember(player.getUniqueId());
        Latifundia.getPlugin(Latifundia.class).getPlayerStalker().updatePlayer(player);
        Latifundia.getPlugin(Latifundia.class).getPlayerStalker().updateChunk(player.getLocation().getChunk());
    }

    public void addMember(Player player) {
        memberList.put(player.getUniqueId(), Rank.SERF);
        Latifundia.getPlugin(Latifundia.class).getCityStateManager().addMember(player.getUniqueId(), this.getCityStateUUID());
        Latifundia.getPlugin(Latifundia.class).getPlayerStalker().updatePlayer(player);
        Latifundia.getPlugin(Latifundia.class).getPlayerStalker().updateChunk(player.getLocation().getChunk());
    }

    public void deleteSelf() {

        this.unclaimAllChunks();

    }

    public Relation getRelation(UUID cityStateUUID) {

        if (relationMap.containsKey(cityStateUUID)) {
            return relationMap.get(cityStateUUID);
        }

        return Relation.NEUTRAL;

    }

    public boolean ownsChunk(Chunk chunk) {
        Point point = new Point(chunk.getX(), chunk.getZ());
        String world = chunk.getWorld().getName();
        if (!worldClaimMap.containsKey(world)) return false;
        List<Point> pointList = worldClaimMap.get(world);
        return pointList.contains(point);
    }

    public void unclaimAllChunks() {

        for (String world : worldClaimMap.keySet()) {

            World bukkitWorld = Bukkit.getWorld(world);

            if (bukkitWorld != null) {
                List<Point> pointList = worldClaimMap.get(world);

                WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
                WorldTree worldTree = worldTreeManager.getWorldTree(bukkitWorld);

                for (Point chunkPoint : pointList) {
                    worldTree.removeClaim(chunkPoint);
                }
            }

        }

        worldClaimMap.clear();

    }

    /**
     * Creates an invitation entry into a Map, noting current time and sending an invitation request.
     * @param player the player who was invited
     */
    public void invitePlayer(Player player) {

        invitedPlayers.put(player.getUniqueId(), System.currentTimeMillis());

        TextComponent message = new TextComponent("You have been invited to: '" + this.name + "', you have 10 minutes to accept: ");
        message.setColor(ChatColor.GREEN);

        TextComponent accept = new TextComponent("[accept]");
        accept.setColor(ChatColor.DARK_GREEN);
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lf accept " + this.name));

        TextComponent decline = new TextComponent("[decline]");
        decline.setColor(ChatColor.DARK_RED);
        decline.setBold(true);
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lf decline " + this.name));

        message.addExtra(accept);
        message.addExtra(new TextComponent(" "));
        message.addExtra(decline);

        player.spigot().sendMessage(message);

    }

    public void acceptInvite(Player player) {

        if (!invitedPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(GeneralUtils.colour("&cInvite not found!"));
            return;
        }

        Long time = invitedPlayers.get(player.getUniqueId());
        Long currentTime = System.currentTimeMillis();

        if (currentTime - time > 600000) {
            player.sendMessage(GeneralUtils.colour("&cInvite has expired."));
            invitedPlayers.remove(player.getUniqueId());
            return;
        }

        this.addMember(player);

    }

    public void sendBroadcast(String message) {

        for (UUID uuid : memberList.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(GeneralUtils.colour(message));
            }
        }

    }

    public void sendActionbarBroadcast(String message) {

        int fadeIn = 5;
        int stay = 20;
        int fadeOut = 10;

        for (UUID uuid : memberList.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitle(GeneralUtils.colour(message), "", fadeIn, stay, fadeOut);
            }
        }

    }


    public boolean claimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        List<Point> pointList = worldClaimMap.get(chunk.getWorld().getName());
        pointList.add(point);
        return worldTree.insertClaim(point, this.cityStateUUID);
    }

    public boolean unclaimChunk(Chunk chunk) {
        WorldTreeManager worldTreeManager = Latifundia.getPlugin(Latifundia.class).getWorldTreeManager();
        WorldTree worldTree = worldTreeManager.getWorldTree(chunk.getWorld());
        Point point = new Point(chunk.getX(), chunk.getZ());
        List<Point> pointList = worldClaimMap.get(chunk.getWorld().getName());
        pointList.remove(point);
        return worldTree.removeClaim(point);
    }

    public boolean canInvite(Player player) {
        Rank rank = getRank(player);
        return (rank == Rank.GENERAL || rank == Rank.LEADER);
    }

    public boolean canClaim(Player player) {
        Rank rank = getRank(player);
        return (rank == Rank.GENERAL || rank == Rank.LEADER || rank == Rank.ELDER);
    }

    public boolean canUnclaim(Player player) {
        Rank rank = getRank(player);
        return (rank == Rank.GENERAL || rank == Rank.LEADER || rank == Rank.ELDER);
    }

    public boolean canKick(Player kicker, Player kickee) {

        Rank kickerRank = getRank(kicker);
        Rank kickeeRank = getRank(kickee);

        if (kickerRank == Rank.LEADER) return true;

        if (kickerRank == Rank.GENERAL && (kickeeRank != Rank.GENERAL && kickeeRank != Rank.LEADER)) return true;

        return false;

    }

    /**
     * Does not check if promotion is allowed
     * @param player
     */
    public void promote(Player player) {
        Rank rank = null;
        switch (this.getRank(player)) {
            case SERF:
                rank = Rank.ELDER;
                break;
            case ELDER:
                rank = Rank.GENERAL;
                break;
            case GENERAL:
                rank = Rank.LEADER;
                break;
        }
        memberList.put(player.getUniqueId(), rank);
    }

    /**
     *
     * @param player The person who is promoting
     * @param desiredRank The desired rank
     * @return
     */
    public boolean canPromote(Player player, Rank desiredRank) {
        Rank rank = getRank(player);
        if (rank == Rank.SERF) return false;
        if (desiredRank == Rank.SERF && (rank == Rank.GENERAL || rank == Rank.LEADER)) return true;
        if (desiredRank == Rank.ELDER && rank == Rank.LEADER) return true;
        if (desiredRank == Rank.GENERAL && rank == Rank.LEADER) return true;
        if (desiredRank == Rank.LEADER && rank == Rank.LEADER) return true;
        return false;
    }

    /**
     * Assumes both players are valid and in the citystate and are not the same
     * @param promoter
     * @param promotee
     * @return
     */
    public boolean canPromote(Player promoter, Player promotee) {

        Rank promoterRank = getRank(promoter);
        Rank promoteeRank = getRank(promotee);

        Rank desiredRank = null;

        switch (promoteeRank) {
            case SERF:
                desiredRank = Rank.ELDER;
                break;
            case ELDER:
                desiredRank = Rank.GENERAL;
                break;
            case GENERAL:
                desiredRank = Rank.LEADER;
                break;
            case LEADER:
                return false;
        }

        return this.canPromote(promoter, desiredRank);

    }

    /**
     * Leader has total power
     * General has total power excluding over the Leader and cannot kick other generals
     * Elder only has the power to claim and unclaim over the Serf
     * Serf can't do shit but it can build
     */
    public enum Rank {
        LEADER,
        GENERAL,
        ELDER,
        SERF
    }

}
