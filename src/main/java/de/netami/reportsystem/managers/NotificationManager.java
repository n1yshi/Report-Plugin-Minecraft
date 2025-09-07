package de.netami.reportsystem.managers;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.Report;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

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
            admin.sendMessage("§c§l[REPORT] §r§eNew " + report.getType().getDisplayName().toLowerCase() + " received!");
            admin.sendMessage("§7ID: #" + report.getId() + " | Reporter: " + report.getReporterName());
            admin.sendMessage("§7Title: " + report.getTitle());
            admin.sendMessage("§7Use /reportadmin to view details");
            
            if (plugin.getConfig().getBoolean("notifications.sound", true)) {
                admin.playSound(admin.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            }
            
            if (plugin.getConfig().getBoolean("notifications.title", true)) {
                admin.sendTitle("§c§lNew Report", "§7#" + report.getId() + " - " + report.getTypeDisplayName(), 10, 40, 10);
            }
        }
        
        plugin.getLogger().info(String.format("New report notification sent to %d admins", admins.size()));
    }
    
    public void notifyReportUpdate(Report report, String action, Player admin) {
        Player reporter = Bukkit.getPlayer(report.getReporterId());
        if (reporter != null && reporter.isOnline()) {
            reporter.sendMessage("§a[Reports] Your report #" + report.getId() + " has been " + action.toLowerCase() + " by " + admin.getName());
            if (report.hasResponse()) {
                reporter.sendMessage("§7Admin Response: " + report.getAdminResponse());
            }
        }
        
        List<Player> admins = getOnlineAdmins();
        admins.remove(admin);
        
        for (Player otherAdmin : admins) {
            otherAdmin.sendMessage("§e[Reports] Report #" + report.getId() + " " + action.toLowerCase() + " by " + admin.getName());
        }
    }
    
    public void notifyAdminOnJoin(Player admin) {
        int openReports = plugin.getReportManager().getOpenReportsCount();
        int inProgressReports = plugin.getReportManager().getInProgressReportsCount();
        
        if (openReports > 0 || inProgressReports > 0) {
            admin.sendMessage("§e[Reports] §7You have pending reports to review:");
            admin.sendMessage("§7Open: §e" + openReports + " §7| In Progress: §e" + inProgressReports);
            admin.sendMessage("§7Use §e/reportadmin §7to manage reports");
        }
    }
    
    private List<Player> getOnlineAdmins() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("reports.notify"))
                .collect(Collectors.toList());
    }
    
    private void startNotificationTask() {
        int interval = plugin.getConfig().getInt("notifications.reminder-interval", 1800);
        
        notificationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<Player> admins = getOnlineAdmins();
            if (admins.isEmpty()) return;
            
            int openReports = plugin.getReportManager().getOpenReportsCount();
            if (openReports > 0) {
                for (Player admin : admins) {
                    admin.sendMessage("§e[Reports] §7Reminder: " + openReports + " open reports need attention");
                }
            }
        }, interval * 20L, interval * 20L);
    }
    
    public void cleanup() {
        if (notificationTask != null) {
            notificationTask.cancel();
        }
    }
}