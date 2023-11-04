package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTree;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CityStateManager {

    private final JavaPlugin plugin;
    private final Map<UUID, CityState> cityStateMap = new HashMap<>();

    public CityStateManager(JavaPlugin plugin) {

        this.plugin = plugin;

    }

    public CityState getCityState(UUID cuuid) {

        return cityStateMap.get(cuuid);

    }

    public void createCityState(String name, Player creator) {



    }

    public void setCityState(CityState cityState) {



    }

    public void loadAllCityStates() {



    }

    public CityState loadCityState(UUID cuuid) {

        return null;

    }

    public void saveCityState(CityState cityState) {



    }

    public void saveAllCityStates(CityState cityState) {



    }

}
