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
        
        // Check if player has admin permissions and notify about pending reports
        if (player.hasPermission("reports.notify")) {
            int openReports = plugin.getReportManager().getOpenReportsCount();
            if (openReports > 0) {
                player.sendMessage("§e[Reports] There are " + openReports + " open reports waiting for review!");
                player.sendMessage("§7Use /reportadmin to view and manage reports.");
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clean up any pending report data for this player
        plugin.getAdminManager().removePlayerFromReportMode(player);
        
        // Note: We don't clear pending inventory data here as it's handled by the InventoryClickListener
        // and we want to preserve it in case the player reconnects quickly
    }
}