package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTree;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

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

        UUID cuuid = UUID.randomUUID();

        cityStateMap.put(cuuid, new CityState(cuuid, name, creator));

    }

    public void setCityState(UUID cuuid, CityState cityState) {

        cityStateMap.put(cuuid, cityState);

    }

    public void loadAllCityStates() {



    }

    public CityState loadCityState(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(Files.newInputStream(Paths.get(filepath))))) {
            return (CityState) in.readObject();
        }
    }

    public void saveCityState(CityState cityState) {



    }

    public void saveAllCityStates(CityState cityState) {



    }

}
