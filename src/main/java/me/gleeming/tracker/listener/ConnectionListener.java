package me.gleeming.tracker.listener;

import me.gleeming.tracker.file.FileManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ConnectionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();
        String domain = player.getPendingConnection().getVirtualHost().getHostString().toLowerCase();

        FileManager.getInstance().handleJoin(player, domain);
    }
}
