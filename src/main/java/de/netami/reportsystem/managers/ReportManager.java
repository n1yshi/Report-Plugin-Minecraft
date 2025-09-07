package de.netami.reportsystem.managers;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.Report;
import de.netami.reportsystem.types.ReportStatus;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ReportManager {
    
    private final NetamiReportSystemPlugin plugin;
    private final Map<Integer, Report> reports;
    private int nextReportId;
    
    public ReportManager(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
        this.reports = new HashMap<>();
        this.nextReportId = 1;
        
        // Load existing reports
        loadReports();
    }
    
    private void loadReports() {
        Map<Integer, Report> loadedReports = plugin.getDatabaseManager().loadAllReports();
        reports.putAll(loadedReports);
        
        // Set next ID
        if (!reports.isEmpty()) {
            nextReportId = reports.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        }
        
        plugin.getLogger().info("Loaded " + reports.size() + " reports from database");
    }
    
    public Report createUserReport(Player reporter, String title, String description, 
                                  Player targetPlayer) {
        Report report = new Report(
            nextReportId++,
            reporter.getUniqueId(),
            reporter.getName(),
            title,
            description,
            reporter.getLocation().clone(),
            targetPlayer.getUniqueId(),
            targetPlayer.getName()
        );
        
        reports.put(report.getId(), report);
        plugin.getDatabaseManager().saveReport(report);
        
        // Notify admins
        plugin.getNotificationManager().notifyNewReport(report);
        
        // Refresh admin UIs
        plugin.getAdminManager().refreshAdminUIs();
        
        plugin.getLogger().info(String.format("New user report #%d: %s reported %s for '%s'", 
            report.getId(), reporter.getName(), targetPlayer.getName(), title));
        
        return report;
    }
    
    public Report createBugReport(Player reporter, String title, String description) {
        Report report = new Report(
            nextReportId++,
            reporter.getUniqueId(),
            reporter.getName(),
            ReportType.BUG,
            title,
            description,
            reporter.getLocation().clone()
        );
        
        reports.put(report.getId(), report);
        plugin.getDatabaseManager().saveReport(report);
        
        // Notify admins
        plugin.getNotificationManager().notifyNewReport(report);
        
        // Refresh admin UIs
        plugin.getAdminManager().refreshAdminUIs();
        
        plugin.getLogger().info(String.format("New bug report #%d: %s reported '%s'", 
            report.getId(), reporter.getName(), title));
        
        return report;
    }
    
    public Report createMapBugReport(Player reporter, String title, String description) {
        plugin.getLogger().info("Creating map bug report - Title: " + title + ", Description: " + description);
        
        Report report = new Report(
            nextReportId++,
            reporter.getUniqueId(),
            reporter.getName(),
            ReportType.MAPBUG,
            title,
            description,
            reporter.getLocation().clone()
        );
        
        reports.put(report.getId(), report);
        plugin.getDatabaseManager().saveReport(report);
        
        plugin.getLogger().info("Map bug report created with ID: " + report.getId());
        plugin.getLogger().info("Total reports now: " + reports.size());
        plugin.getLogger().info("Open reports: " + getOpenReportsCount());
        plugin.getLogger().info("MAPBUG reports: " + getReportsCountByType(ReportType.MAPBUG));
        
        // Notify admins
        plugin.getNotificationManager().notifyNewReport(report);
        
        // Refresh admin UIs
        plugin.getAdminManager().refreshAdminUIs();
        
        plugin.getLogger().info(String.format("New map bug report #%d: %s reported '%s'", 
            report.getId(), reporter.getName(), title));
        
        return report;
    }
    
    public Report getReport(int id) {
        return reports.get(id);
    }
    
    public List<Report> getAllReports() {
        return new ArrayList<>(reports.values());
    }
    
    public List<Report> getReportsByStatus(ReportStatus status) {
        return reports.values().stream()
            .filter(report -> report.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    public List<Report> getReportsByType(ReportType type) {
        plugin.getLogger().info("Getting reports by type: " + type);
        plugin.getLogger().info("Total reports in system: " + reports.size());
        
        List<Report> filteredReports = new ArrayList<>();
        for (Report report : reports.values()) {
            plugin.getLogger().info("Report #" + report.getId() + " - Type: " + report.getType() + " - Title: " + report.getTitle());
            if (report.getType() == type) {
                filteredReports.add(report);
                plugin.getLogger().info("  -> MATCHES! Added to filtered list");
            } else {
                plugin.getLogger().info("  -> Does not match " + type);
            }
        }
        
        plugin.getLogger().info("Filtered reports count: " + filteredReports.size());
        return filteredReports;
    }
    
    public List<Report> getReportsByReporter(UUID reporterId) {
        return reports.values().stream()
            .filter(report -> report.getReporterId().equals(reporterId))
            .collect(Collectors.toList());
    }
    
    public List<Report> getReportsByAssignedAdmin(UUID adminId) {
        return reports.values().stream()
            .filter(report -> report.getAssignedAdmin() != null && 
                            report.getAssignedAdmin().equals(adminId))
            .collect(Collectors.toList());
    }
    
    public List<Report> getOpenReports() {
        return getReportsByStatus(ReportStatus.OPEN);
    }
    
    public List<Report> getInProgressReports() {
        return getReportsByStatus(ReportStatus.IN_PROGRESS);
    }
    
    public List<Report> getClosedReports() {
        return reports.values().stream()
            .filter(Report::isClosed)
            .collect(Collectors.toList());
    }
    
    public void assignReport(int reportId, Player admin) {
        Report report = reports.get(reportId);
        if (report != null) {
            report.setAssignedAdmin(admin.getUniqueId(), admin.getName());
            report.setStatus(ReportStatus.IN_PROGRESS);
            plugin.getDatabaseManager().saveReport(report);
            
            // Notify reporter
            Player reporter = Bukkit.getPlayer(report.getReporterId());
            if (reporter != null && reporter.isOnline()) {
                reporter.sendMessage("§a[Reports] Your report #" + reportId + " has been assigned to " + admin.getName());
            }
            
            plugin.getLogger().info(String.format("Report #%d assigned to %s", reportId, admin.getName()));
        }
    }
    
    public void closeReport(int reportId, Player admin, String response) {
        Report report = reports.get(reportId);
        if (report != null) {
            report.setStatus(ReportStatus.CLOSED);
            report.setAdminResponse(response);
            if (!report.hasAssignedAdmin()) {
                report.setAssignedAdmin(admin.getUniqueId(), admin.getName());
            }
            plugin.getDatabaseManager().saveReport(report);
            
            // Notify reporter
            Player reporter = Bukkit.getPlayer(report.getReporterId());
            if (reporter != null && reporter.isOnline()) {
                reporter.sendMessage("§c[Reports] Your report #" + reportId + " has been closed by " + admin.getName());
                if (response != null && !response.trim().isEmpty()) {
                    reporter.sendMessage("§7Response: " + response);
                }
            }
            
            plugin.getLogger().info(String.format("Report #%d closed by %s", reportId, admin.getName()));
        }
    }
    
    public void resolveReport(int reportId, Player admin, String response) {
        Report report = reports.get(reportId);
        if (report != null) {
            report.setStatus(ReportStatus.RESOLVED);
            report.setAdminResponse(response);
            if (!report.hasAssignedAdmin()) {
                report.setAssignedAdmin(admin.getUniqueId(), admin.getName());
            }
            plugin.getDatabaseManager().saveReport(report);
            
            // Notify reporter
            Player reporter = Bukkit.getPlayer(report.getReporterId());
            if (reporter != null && reporter.isOnline()) {
                reporter.sendMessage("§a[Reports] Your report #" + reportId + " has been resolved by " + admin.getName());
                if (response != null && !response.trim().isEmpty()) {
                    reporter.sendMessage("§7Response: " + response);
                }
            }
            
            plugin.getLogger().info(String.format("Report #%d resolved by %s", reportId, admin.getName()));
        }
    }
    
    public void reopenReport(int reportId, Player admin) {
        Report report = reports.get(reportId);
        if (report != null && report.isClosed()) {
            report.setStatus(ReportStatus.OPEN);
            plugin.getDatabaseManager().saveReport(report);
            
            // Notify reporter
            Player reporter = Bukkit.getPlayer(report.getReporterId());
            if (reporter != null && reporter.isOnline()) {
                reporter.sendMessage("§e[Reports] Your report #" + reportId + " has been reopened by " + admin.getName());
            }
            
            plugin.getLogger().info(String.format("Report #%d reopened by %s", reportId, admin.getName()));
        }
    }
    
    public void deleteReport(int reportId) {
        Report report = reports.remove(reportId);
        if (report != null) {
            plugin.getDatabaseManager().deleteReport(reportId);
            plugin.getLogger().info(String.format("Report #%d deleted", reportId));
        }
    }
    
    public void teleportToReport(Player admin, int reportId) {
        Report report = reports.get(reportId);
        if (report != null && report.getLocation() != null) {
            Location loc = report.getLocation();
            if (loc.getWorld() != null) {
                admin.teleport(loc);
                admin.sendMessage("§a[Reports] Teleported to report #" + reportId + " location");
            } else {
                admin.sendMessage("§c[Reports] Report location world is not loaded!");
            }
        } else {
            admin.sendMessage("§c[Reports] Report not found or has no location!");
        }
    }
    
    // Statistics
    public int getTotalReports() {
        return reports.size();
    }
    
    public int getOpenReportsCount() {
        return getOpenReports().size();
    }
    
    public int getInProgressReportsCount() {
        return getInProgressReports().size();
    }
    
    public int getClosedReportsCount() {
        return getClosedReports().size();
    }
    
    public int getReportsCountByType(ReportType type) {
        return getReportsByType(type).size();
    }
    
    public Map<ReportType, Integer> getReportCountsByType() {
        Map<ReportType, Integer> counts = new HashMap<>();
        for (ReportType type : ReportType.values()) {
            counts.put(type, getReportsCountByType(type));
        }
        return counts;
    }
    
    public Map<ReportStatus, Integer> getReportCountsByStatus() {
        Map<ReportStatus, Integer> counts = new HashMap<>();
        for (ReportStatus status : ReportStatus.values()) {
            counts.put(status, getReportsByStatus(status).size());
        }
        return counts;
    }
    
    public boolean canPlayerReport(Player player) {
        // Check cooldown
        long lastReport = plugin.getDatabaseManager().getLastReportTime(player.getUniqueId());
        long cooldown = plugin.getConfig().getLong("report-cooldown", 300) * 1000; // 5 minutes default
        
        return System.currentTimeMillis() - lastReport >= cooldown;
    }
    
    public long getRemainingCooldown(Player player) {
        long lastReport = plugin.getDatabaseManager().getLastReportTime(player.getUniqueId());
        long cooldown = plugin.getConfig().getLong("report-cooldown", 300) * 1000;
        long remaining = cooldown - (System.currentTimeMillis() - lastReport);
        
        return Math.max(0, remaining / 1000); // Return seconds
    }
    
    /**
     * Resets all reports and statistics - DANGER ZONE!
     */
    public void resetAllReports() {
        // Clear all reports from memory
        reports.clear();
        
        // Reset the next report ID
        nextReportId = 1;
        
        // Clear all reports from database
        plugin.getDatabaseManager().deleteAllReports();
        
        plugin.getLogger().warning("ALL REPORTS HAVE BEEN RESET! All data has been permanently deleted.");
    }
}