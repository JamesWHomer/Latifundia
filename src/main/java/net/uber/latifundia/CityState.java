package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.Claim;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;

public class CityState implements Serializable {
    private static final long serialVersionUID = 1L;

    // Planning on implementing Empires but not yet.

    private String name;
    private UUID cityStateUUID;
    private Map<UUID, Rank> memberList = new HashMap<>();
    private List<Claim> claims = new ArrayList<>();

    /**
     * Creates new city state from scratch.
     * @param name Creates name of CityState
     * @param creator Clarifies who is Praetor
     */
    public CityState(UUID cuuid, String name, Player creator) {
        this.name = name;
        this.cityStateUUID = cuuid;
        this.claims.add(new Claim(creator.getUniqueId(), creator.getLocation()));
        this.memberList.put(creator.getUniqueId(), Rank.LEADER);
    }

    public String getName() {
        return this.name;
    }

    public Set<UUID> getMembers() {
        return memberList.keySet();
    }

    public UUID getCityStateUUID() {
        return this.cityStateUUID;
    }

    enum Rank {
        LEADER,
        COLEADER,
        COMMANDER,
        CITIZEN
    }

}
