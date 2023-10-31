package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.Claim;
import org.bukkit.entity.Player;

import java.util.*;

public class CityState {

    // Planning on implementing Empires but not yet.

    private String name;
    private UUID cityStateUUID;
    private Map<UUID, Rank> memberList = new HashMap<>();
    private List<Claim> claims = new ArrayList<>();

    /**
     * Parses in Json file and loads CityState.
     */
    public CityState() {



    }

    /**
     * Creates new city state from scratch.
     * @param name Creates name of CityState
     * @param creator Clarifies who is Praetor
     */
    public CityState(String name, Player creator) {
        this.name = name;
        this.cityStateUUID = UUID.randomUUID();
        this.claims.add(new Claim(creator.getUniqueId(), creator.getLocation()));
        this.memberList.put(creator.getUniqueId(), Rank.CONSUL);
    }

    enum Rank {
        IMPERATOR,
        CONSUL,
        LICTOR,
        PLEBIAN
    }

}
