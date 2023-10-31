package net.uber.latifundia;

import net.uber.latifundia.claimmanagement.WorldTreeManager;
import net.uber.latifundia.commands.LatifundiaCommand;
import net.uber.latifundia.commands.LatifundiaTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Latifundia extends JavaPlugin {

    private WorldTreeManager worldTreeManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        worldTreeManager = new WorldTreeManager();

        this.getCommand("latifundia").setExecutor(new LatifundiaCommand());
        this.getCommand("latifundia").setTabCompleter(new LatifundiaTabCompleter());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public WorldTreeManager getWorldTreeManager() {
        return worldTreeManager;
    }

}
