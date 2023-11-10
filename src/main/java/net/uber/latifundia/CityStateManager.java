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
    //First value is citystate's uuid and second value is citystate object
    private final Map<UUID, CityState> cityStateMap = new HashMap<>();
    //First value is player, second is the city
    private final Map<UUID, UUID> playerCityStateMap = new HashMap<>();

    public CityStateManager(JavaPlugin plugin) {

        this.plugin = plugin;
        loadAllCityStates();

    }

    public boolean doesCityExist(UUID cuuid) {

        return cityStateMap.containsKey(cuuid);

    }

    public boolean isCitizen(Player player) {
        return playerCityStateMap.containsKey(player.getUniqueId());
    }

    public CityState getCityState(UUID cityStateUUID) {

        return cityStateMap.get(cityStateUUID);

    }

    public CityState getCityState(Player player) {

        return getCityState(playerCityStateMap.get(player.getUniqueId()));

    }

    public void createCityState(String name, Player creator) {

        UUID cuuid = UUID.randomUUID();

        cityStateMap.put(cuuid, new CityState(cuuid, name, creator));
        playerCityStateMap.put(creator.getUniqueId(), cuuid);

    }

    public void setCityState(CityState cityState) {

        cityStateMap.put(cityState.getCityStateUUID(), cityState);

    }

    public void deleteCityState(UUID cityStateUUID) {

        CityState cityState = this.getCityState(cityStateUUID);

        this.cityStateMap.remove(cityStateUUID);

        Set<UUID> members = cityState.getMembers();

        for (UUID member : members) {
            playerCityStateMap.remove(member);
        }

        cityState.deleteSelf();

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
            return; // No city states to load if the directory didn't exist.
        }

        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".citystate"));
        if (files == null) {
            plugin.getLogger().warning("No city state files found, or an I/O error occurred.");
            return;
        }

        for (File file : files) {
            try {
                // We assume that the file name is the UUID of the city state.
                String filename = file.getName();
                UUID cuuid = UUID.fromString(filename.substring(0, filename.length() - ".citystate".length()));
                CityState cityState = loadCityState(file.getAbsolutePath());
                cityStateMap.put(cuuid, cityState);
            } catch (IOException | ClassNotFoundException e) {
                plugin.getLogger().severe("Could not load CityState from file: " + file.getName());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("File name " + file.getName() + " does not correspond to a valid UUID.");
                e.printStackTrace();
            }
        }
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
