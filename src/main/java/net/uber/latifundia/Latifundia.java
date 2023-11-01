package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTreeManager;
import net.uber.latifundia.commands.LatifundiaCommand;
import net.uber.latifundia.commands.LatifundiaTabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Latifundia extends JavaPlugin implements Listener {

    private WorldTreeManager worldTreeManager;
    private PlayerStalker playerStalker;

    @Override
    public void onEnable() {
        // Plugin startup logic

        worldTreeManager = new WorldTreeManager(this);

        this.getCommand("latifundia").setExecutor(new LatifundiaCommand());
        this.getCommand("latifundia").setTabCompleter(new LatifundiaTabCompleter());

        getServer().getPluginManager().registerEvents(this, this);

        playerStalker = new PlayerStalker(worldTreeManager);

        getServer().getPluginManager().registerEvents(playerStalker, this);

    }

    @Override
    public void onDisable() {
        // Save data on shutdown
        worldTreeManager.saveAllWorldTrees();
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        // Save data on world save
        worldTreeManager.saveAllWorldTreesAsync();
    }

    public WorldTreeManager getWorldTreeManager() {
        return worldTreeManager;
    }

}
