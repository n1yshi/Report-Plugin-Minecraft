package de.netami.reportsystem.listeners;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.managers.AdminManager;
import de.netami.reportsystem.types.Report;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    
    private final NetamiReportSystemPlugin plugin;
    
    public PlayerChatListener(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().trim();
        
        plugin.getLogger().info("Chat event - Player: " + player.getName() + ", Message: " + message);
        
        if (!plugin.getAdminManager().isPlayerInReportMode(player)) {
            plugin.getLogger().info("Player " + player.getName() + " is not in report mode, ignoring chat");
            return;
        }
        
        plugin.getLogger().info("Player " + player.getName() + " is in report mode, processing chat message");
        event.setCancelled(true);
        
        if (message.equalsIgnoreCase("cancel")) {
            plugin.getLogger().info("Player " + player.getName() + " cancelled report process");
            plugin.getAdminManager().removePlayerFromReportMode(player);
            player.sendMessage("§c[Reports] Process cancelled.");
            return;
        }
        
        AdminManager.ReportMode mode = plugin.getAdminManager().getPlayerReportMode(player);
        
        if (mode == null) {
            plugin.getLogger().warning("Report mode is null for player " + player.getName());
            plugin.getAdminManager().removePlayerFromReportMode(player);
            player.sendMessage("§c[Reports] Error: Report mode not found. Process cancelled.");
            return;
        }
        
        plugin.getLogger().info("Processing report mode for " + player.getName() + " - Type: " + mode.getType() + ", IsAdminResponse: " + mode.isAdminResponse());
        
        if (mode.isAdminResponse()) {
            if (mode.getAction().equals("reset")) {
                handleResetConfirmation(player, message, mode);
            } else {
                handleAdminResponse(player, message, mode);
            }
        } else {
            handleReportCreation(player, message, mode);
        }
    }
    
    private void handleAdminResponse(Player admin, String message, AdminManager.ReportMode mode) {
        int reportId = mode.getReportId();
        String action = mode.getAction();
        
        if (action.equals("close")) {
            plugin.getReportManager().closeReport(reportId, admin, message);
            admin.sendMessage("§a[Reports] Report #" + reportId + " has been closed!");
        } else if (action.equals("resolve")) {
            plugin.getReportManager().resolveReport(reportId, admin, message);
            admin.sendMessage("§a[Reports] Report #" + reportId + " has been resolved!");
        }
        
        plugin.getAdminManager().removePlayerFromReportMode(admin);
        
        // Reopen report details
        Report report = plugin.getReportManager().getReport(reportId);
        if (report != null) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getAdminManager().openReportDetails(admin, report);
            });
        }
    }
    
    private void handleResetConfirmation(Player admin, String message, AdminManager.ReportMode mode) {
        if (message.equalsIgnoreCase("CONFIRM RESET")) {
            // Check if admin has permission
            if (!admin.hasPermission("reports.reset")) {
                admin.sendMessage("§c[Reports] You don't have permission to reset reports!");
                plugin.getAdminManager().removePlayerFromReportMode(admin);
                return;
            }
            
            // Execute the reset on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getAdminManager().executeResetAllReports(admin);
                plugin.getAdminManager().removePlayerFromReportMode(admin);
            });
        } else {
            admin.sendMessage("§c[Reports] Reset cancelled. You must type 'CONFIRM RESET' exactly to proceed.");
            admin.sendMessage("§7Type 'cancel' to abort or 'CONFIRM RESET' to proceed.");
        }
    }
    
    private void handleReportCreation(Player player, String message, AdminManager.ReportMode mode) {
        plugin.getLogger().info("Handling report creation for " + player.getName() + " - Message: " + message);
        plugin.getLogger().info("Mode details - Type: " + mode.getType() + ", CustomTitle: " + mode.isCustomTitle() + ", CurrentTitle: " + mode.getTitle());
        
        if (mode.isCustomTitle() && mode.getTitle() == null) {
            // First message is the custom title
            plugin.getLogger().info("Setting custom title for " + player.getName() + ": " + message);
            mode.setTitle(message);
            player.sendMessage("§e[Reports] Title set: §f" + message);
            player.sendMessage("§e[Reports] Now please provide a detailed description:");
            player.sendMessage("§7Type 'cancel' to cancel the report process.");
        } else {
            // This is the description - make all variables final for lambda
            final String finalTitle = mode.getTitle() != null ? mode.getTitle() : "Report";
            final String finalDescription = message;
            final de.netami.reportsystem.types.ReportType finalType = mode.getType();
            final Player finalTargetPlayer = mode.getTargetPlayer(); // Can be null for non-user reports
            
            plugin.getLogger().info("Creating report for " + player.getName() + " - Title: " + finalTitle + ", Type: " + finalType + ", Description length: " + finalDescription.length());
            
            // Create the report directly on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                createReport(player, finalType, finalTitle, finalDescription, finalTargetPlayer);
                plugin.getAdminManager().removePlayerFromReportMode(player);
                
                // Pending data cleanup is handled by the InventoryClickListener
            });
        }
    }
    
    private void createReport(Player reporter, de.netami.reportsystem.types.ReportType type, String title, String description, Player targetPlayer) {
        de.netami.reportsystem.types.Report report;
        
        switch (type) {
            case USER:
                if (targetPlayer == null) {
                    reporter.sendMessage("§c[Reports] Error: Target player not found!");
                    return;
                }
                report = plugin.getReportManager().createUserReport(reporter, title, description, targetPlayer);
                break;
                
            case BUG:
                report = plugin.getReportManager().createBugReport(reporter, title, description);
                break;
                
            case MAPBUG:
                report = plugin.getReportManager().createMapBugReport(reporter, title, description);
                break;
                
            default:
                reporter.sendMessage("§c[Reports] Error: Invalid report type!");
                return;
        }
        
        // Send enhanced confirmation with UI feedback
        reporter.sendMessage("§a[Reports] Your report has been submitted successfully!");
        reporter.sendMessage("§7Report ID: #" + report.getId());
        reporter.sendMessage("§7Type: " + report.getTypeDisplayName());
        reporter.sendMessage("§7Location: " + report.getLocationString());
        if (report.getTargetPlayerName() != null) {
            reporter.sendMessage("§7Target: " + report.getTargetPlayerName());
        }
        reporter.sendMessage("§7You will be notified when an admin reviews your report.");
        
        // Play success sound and show title
        reporter.playSound(reporter.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        reporter.sendTitle("§a§lReport Submitted", "§7ID: #" + report.getId(), 10, 40, 10);
        
        // Clear any pending inventory data
        if (plugin.getServer().getPluginManager().getPlugin("NetamiReportSystem") != null) {
            // Clear pending data from inventory listener
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                // This ensures any UI state is properly cleared
                plugin.getLogger().info("Report #" + report.getId() + " created successfully via chat interface");
            });
        }
    }
}