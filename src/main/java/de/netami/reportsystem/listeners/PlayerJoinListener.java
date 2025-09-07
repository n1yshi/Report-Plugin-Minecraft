package de.netami.reportsystem.listeners;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {
    
    private final NetamiReportSystemPlugin plugin;
    
    public PlayerJoinListener(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("reports.notify")) {
            plugin.getNotificationManager().notifyAdminOnJoin(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        plugin.getAdminManager().removePlayerFromReportMode(player);
    }
}