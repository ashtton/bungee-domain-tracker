package me.gleeming.tracker;

import lombok.Getter;
import me.gleeming.tracker.command.DomainCommand;
import me.gleeming.tracker.command.DomainsCommand;
import me.gleeming.tracker.file.FileManager;
import me.gleeming.tracker.listener.ConnectionListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class DomainTracker extends Plugin {
    @Getter private static DomainTracker instance;

    public void onEnable() {
        instance = this;

        // Initialize Managers
        new FileManager();

        // Register Listeners
        BungeeCord.getInstance().getPluginManager().registerListener(this, new ConnectionListener());

        // Register Commands
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new DomainsCommand());
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new DomainCommand());
    }

    /**
     * Runs a task asynchronously
     * @param task Task
     */
    public void async(Runnable task) { BungeeCord.getInstance().getScheduler().runAsync(this, task); }
}
