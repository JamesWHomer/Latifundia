package net.uber.latifundia;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CityStateManager {

    private final JavaPlugin plugin;
    private final Map<UUID, CityState> cityStateMap = new HashMap<>();
    //First value is player, second is the city
    private final Map<UUID, UUID> playerCityStateMap = new HashMap<>();

    public CityStateManager(JavaPlugin plugin) {

        this.plugin = plugin;

    }

    public CityState getCityState(UUID cuuid) {

        return cityStateMap.get(cuuid);

    }

    public CityState getCityState(Player player) {

        return getCityState(playerCityStateMap.get(player.getUniqueId()));

    }

    public void createCityState(String name, Player creator) {

        UUID cuuid = UUID.randomUUID();

        cityStateMap.put(cuuid, new CityState(cuuid, name, creator));

    }

    public void setCityState(UUID cuuid, CityState cityState) {

        cityStateMap.put(cuuid, cityState);

    }

    public CityState loadCityState(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(Files.newInputStream(Paths.get(filepath))))) {
            CityState cityState = (CityState) in.readObject();
            Set<UUID> cityMembers = cityState.getMembers();
            for (UUID pUUID : cityMembers) {
                playerCityStateMap.put(pUUID, cityState.getCityStateUUID());
            }
            return cityState;
        }
    }

    public void loadCityStateAsync(String filepath, Consumer<CityState> callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                CityState cityState = loadCityState(filepath);
                plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(cityState));
            } catch (IOException | ClassNotFoundException e) {
                plugin.getLogger().severe("Could not load CityState asynchronously");
                e.printStackTrace();
            }
        });
    }

    private void loadAllCityStates() {
        File dataFolder = new File(plugin.getDataFolder(), "cityStates");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        //Have not implemented yet

    }

    public void saveAllCityStates() {
        File dataFolder = new File(plugin.getDataFolder(), "cityStates");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        for (Map.Entry<UUID, CityState> entry : cityStateMap.entrySet()) {
            try {
                File worldFile = new File(dataFolder, entry.getKey() + ".citystate");
                saveCityState(entry.getValue(), worldFile.getAbsolutePath());
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save CityState for world " + entry.getKey());
                e.printStackTrace();
            }
        }
    }

    public void saveAllCityStatesAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            saveAllCityStates();
        });
    }


    public void saveCityState(CityState cityState, String filepath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(Files.newOutputStream(Paths.get(filepath))))) {
            out.writeObject(cityState);
        }
    }

    public void saveCityStateAsync(CityState cityState, String filepath) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                saveCityState(cityState, filepath);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save CityState asynchronously");
                e.printStackTrace();
            }
        });
    }

}
