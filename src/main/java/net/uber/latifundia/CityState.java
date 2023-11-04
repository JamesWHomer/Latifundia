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
     * Parses in .citystate file and loads CityState.
     */
    public CityState() {



    }

    /**
     * Creates new city state from scratch.
     * @param name Creates name of CityState
     * @param creator Clarifies who is Praetor
     */
    public CityState(UUID uuid, String name, Player creator) {
        this.name = name;
        this.cityStateUUID = UUID.randomUUID();
        this.claims.add(new Claim(creator.getUniqueId(), creator.getLocation()));
        this.memberList.put(creator.getUniqueId(), Rank.LEADER);
    }

    enum Rank {
        LEADER,
        COLEADER,
        COMMANDER,
        CITIZEN
    }

}
