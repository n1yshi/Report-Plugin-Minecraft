package de.netami.reportsystem.managers;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.Report;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    
    private final NetamiReportSystemPlugin plugin;
    private BukkitTask notificationTask;
    
    public NotificationManager(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
        startNotificationTask();
    }
    
    public void notifyNewReport(Report report) {
        List<Player> admins = getOnlineAdmins();
        
        for (Player admin : admins) {
            // Send notification message
            admin.sendMessage("§c§l[REPORT] §r§eNew " + report.getType().getDisplayName().toLowerCase() + " received!");
            admin.sendMessage("§7ID: §f#" + report.getId());
            admin.sendMessage("§7Reporter: §f" + report.getReporterName());
            admin.sendMessage("§7Title: §f" + report.getTitle());
            if (report.getTargetPlayerName() != null) {
                admin.sendMessage("§7Target: §f" + report.getTargetPlayerName());
            }
            admin.sendMessage("§7Use §e/reportadmin §7to manage reports");
            
            // Play notification sound
            if (plugin.getConfig().getBoolean("notifications.sound", true)) {
                admin.playSound(admin.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            }
            
            // Send title notification
            if (plugin.getConfig().getBoolean("notifications.title", true)) {
                admin.sendTitle("§c§lNew Report", "§e#" + report.getId() + " - " + report.getTitle(), 10, 40, 10);
            }
        }
        
        // Log to console
        plugin.getLogger().info(String.format("New report notification sent to %d admins", admins.size()));
    }
    
    public void notifyReportUpdate(Report report, String action, Player admin) {
        // Notify the reporter
        Player reporter = Bukkit.getPlayer(report.getReporterId());
        if (reporter != null && reporter.isOnline()) {
            reporter.sendMessage("§a[Reports] Your report #" + report.getId() + " has been " + action + " by " + admin.getName());
            
            if (plugin.getConfig().getBoolean("notifications.sound", true)) {
                reporter.playSound(reporter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
        
        // Notify other admins
        List<Player> admins = getOnlineAdmins();
        for (Player otherAdmin : admins) {
            if (!otherAdmin.equals(admin)) {
                otherAdmin.sendMessage("§b[Reports] " + admin.getName() + " " + action + " report #" + report.getId());
            }
        }
    }
    
    private void startNotificationTask() {
        // Periodic reminder for open reports
        int interval = plugin.getConfig().getInt("notifications.reminder-interval", 1800); // 30 minutes default
        
        notificationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<Report> openReports = plugin.getReportManager().getOpenReports();
            
            if (!openReports.isEmpty()) {
                List<Player> admins = getOnlineAdmins();
                
                for (Player admin : admins) {
                    if (plugin.getConfig().getBoolean("notifications.reminders", true)) {
                        admin.sendMessage("§e[Reports] Reminder: " + openReports.size() + " open reports need attention!");
                        admin.sendMessage("§7Use §e/reportadmin §7to manage reports");
                    }
                }
            }
        }, interval * 20L, interval * 20L); // Convert seconds to ticks
    }
    
    public void sendReportSummary(Player admin) {
        int totalReports = plugin.getReportManager().getTotalReports();
        int openReports = plugin.getReportManager().getOpenReportsCount();
        int inProgressReports = plugin.getReportManager().getInProgressReportsCount();
        int myReports = plugin.getReportManager().getReportsByAssignedAdmin(admin.getUniqueId()).size();
        
        admin.sendMessage("§6§l=== Report Summary ===");
        admin.sendMessage("§7Total Reports: §f" + totalReports);
        admin.sendMessage("§7Open Reports: §a" + openReports);
        admin.sendMessage("§7In Progress: §e" + inProgressReports);
        admin.sendMessage("§7My Reports: §b" + myReports);
        admin.sendMessage("§7Use §e/reportadmin §7for detailed management");
    }
    
    public void broadcastReportClosed(Report report, Player admin) {
        if (plugin.getConfig().getBoolean("notifications.broadcast-closures", false)) {
            String message = "§a[Reports] Report #" + report.getId() + " (" + report.getType().getDisplayName() + ") has been resolved by " + admin.getName();
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("reports.notify")) {
                    player.sendMessage(message);
                }
            }
        }
    }
    
    private List<Player> getOnlineAdmins() {
        List<Player> admins = new ArrayList<>();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("reports.notify")) {
                admins.add(player);
            }
        }
        
        return admins;
    }
    
    public void cleanup() {
        if (notificationTask != null) {
            notificationTask.cancel();
        }
    }
}