package de.netami.reportsystem.managers;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.Report;
import de.netami.reportsystem.types.ReportStatus;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    
    private final NetamiReportSystemPlugin plugin;
    private final File reportsFile;
    private final File cooldownFile;
    private FileConfiguration reportsConfig;
    private FileConfiguration cooldownConfig;
    
    private final Map<UUID, Long> reportCooldowns;
    
    public DatabaseManager(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
        this.reportsFile = new File(plugin.getDataFolder(), "reports.yml");
        this.cooldownFile = new File(plugin.getDataFolder(), "cooldowns.yml");
        this.reportCooldowns = new HashMap<>();
        
        initializeFiles();
        loadCooldowns();
    }
    
    private void initializeFiles() {
        if (!reportsFile.exists()) {
            try {
                reportsFile.getParentFile().mkdirs();
                reportsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create reports.yml file!");
                e.printStackTrace();
                return;
            }
        }
        
        if (!cooldownFile.exists()) {
            try {
                cooldownFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create cooldowns.yml file!");
                e.printStackTrace();
                return;
            }
        }
        
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
        cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
    }
    
    public void saveReport(Report report) {
        String path = "reports." + report.getId() + ".";
        
        reportsConfig.set(path + "reporter-id", report.getReporterId().toString());
        reportsConfig.set(path + "reporter-name", report.getReporterName());
        reportsConfig.set(path + "type", report.getType().name());
        reportsConfig.set(path + "title", report.getTitle());
        reportsConfig.set(path + "description", report.getDescription());
        reportsConfig.set(path + "status", report.getStatus().name());
        reportsConfig.set(path + "created-at", report.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        reportsConfig.set(path + "updated-at", report.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        if (report.getLocation() != null) {
            Location loc = report.getLocation();
            reportsConfig.set(path + "location.world", loc.getWorld().getName());
            reportsConfig.set(path + "location.x", loc.getX());
            reportsConfig.set(path + "location.y", loc.getY());
            reportsConfig.set(path + "location.z", loc.getZ());
            reportsConfig.set(path + "location.yaw", loc.getYaw());
            reportsConfig.set(path + "location.pitch", loc.getPitch());
        }
        
        if (report.getAssignedAdmin() != null) {
            reportsConfig.set(path + "assigned-admin-id", report.getAssignedAdmin().toString());
            reportsConfig.set(path + "assigned-admin-name", report.getAssignedAdminName());
        }
        
        if (report.getAdminResponse() != null) {
            reportsConfig.set(path + "admin-response", report.getAdminResponse());
        }
        
        if (report.getClosedAt() != null) {
            reportsConfig.set(path + "closed-at", report.getClosedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        if (report.getTargetPlayerId() != null) {
            reportsConfig.set(path + "target-player-id", report.getTargetPlayerId().toString());
            reportsConfig.set(path + "target-player-name", report.getTargetPlayerName());
        }
        
        reportCooldowns.put(report.getReporterId(), System.currentTimeMillis());
        saveCooldowns();
        
        saveReportsFile();
    }
    
    public Map<Integer, Report> loadAllReports() {
        Map<Integer, Report> reports = new HashMap<>();
        
        if (!reportsConfig.contains("reports")) {
            return reports;
        }
        
        for (String idStr : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
            try {
                int id = Integer.parseInt(idStr);
                Report report = loadReport(id);
                if (report != null) {
                    reports.put(id, report);
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid report ID in database: " + idStr);
            }
        }
        
        return reports;
    }
    
    private Report loadReport(int id) {
        String path = "reports." + id + ".";
        
        if (!reportsConfig.contains(path + "reporter-id")) {
            return null;
        }
        
        try {
            UUID reporterId = UUID.fromString(reportsConfig.getString(path + "reporter-id"));
            String reporterName = reportsConfig.getString(path + "reporter-name");
            ReportType type = ReportType.valueOf(reportsConfig.getString(path + "type"));
            String title = reportsConfig.getString(path + "title");
            String description = reportsConfig.getString(path + "description");
            ReportStatus status = ReportStatus.valueOf(reportsConfig.getString(path + "status"));
            
            Location location = null;
            if (reportsConfig.contains(path + "location.world")) {
                String worldName = reportsConfig.getString(path + "location.world");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    double x = reportsConfig.getDouble(path + "location.x");
                    double y = reportsConfig.getDouble(path + "location.y");
                    double z = reportsConfig.getDouble(path + "location.z");
                    float yaw = (float) reportsConfig.getDouble(path + "location.yaw");
                    float pitch = (float) reportsConfig.getDouble(path + "location.pitch");
                    location = new Location(world, x, y, z, yaw, pitch);
                }
            }
            
            Report report;
            if (type == ReportType.USER && reportsConfig.contains(path + "target-player-id")) {
                UUID targetId = UUID.fromString(reportsConfig.getString(path + "target-player-id"));
                String targetName = reportsConfig.getString(path + "target-player-name");
                report = new Report(id, reporterId, reporterName, title, description, location, targetId, targetName);
            } else {
                report = new Report(id, reporterId, reporterName, type, title, description, location);
            }
            
            report.setStatus(status);
            
            if (reportsConfig.contains(path + "assigned-admin-id")) {
                UUID adminId = UUID.fromString(reportsConfig.getString(path + "assigned-admin-id"));
                String adminName = reportsConfig.getString(path + "assigned-admin-name");
                report.setAssignedAdmin(adminId, adminName);
            }
            
            if (reportsConfig.contains(path + "admin-response")) {
                report.setAdminResponse(reportsConfig.getString(path + "admin-response"));
            }
            
            return report;
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error loading report #" + id + ": " + e.getMessage());
            return null;
        }
    }
    
    public void deleteReport(int id) {
        reportsConfig.set("reports." + id, null);
        saveReportsFile();
    }
    
    public void saveAllReports() {
        saveReportsFile();
        saveCooldowns();
    }
    
    private void saveReportsFile() {
        try {
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save reports.yml!");
            e.printStackTrace();
        }
    }
    
    private void loadCooldowns() {
        if (cooldownConfig.contains("cooldowns")) {
            for (String uuidStr : cooldownConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    long time = cooldownConfig.getLong("cooldowns." + uuidStr);
                    reportCooldowns.put(uuid, time);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in cooldowns: " + uuidStr);
                }
            }
        }
    }
    
    private void saveCooldowns() {
        for (Map.Entry<UUID, Long> entry : reportCooldowns.entrySet()) {
            cooldownConfig.set("cooldowns." + entry.getKey().toString(), entry.getValue());
        }
        
        try {
            cooldownConfig.save(cooldownFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save cooldowns.yml!");
            e.printStackTrace();
        }
    }
    
    public long getLastReportTime(UUID playerId) {
        return reportCooldowns.getOrDefault(playerId, 0L);
    }
    
    public void setLastReportTime(UUID playerId, long time) {
        reportCooldowns.put(playerId, time);
        saveCooldowns();
    }
    
    public void reloadData() {
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
        cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
        reportCooldowns.clear();
        loadCooldowns();
    }
    
    public void deleteAllReports() {
        reportsConfig.set("reports", null);
        
        reportCooldowns.clear();
        cooldownConfig.set("cooldowns", null);
        
        saveReportsFile();
        saveCooldowns();
        
        plugin.getLogger().warning("ALL REPORTS AND COOLDOWNS HAVE BEEN DELETED FROM DATABASE!");
    }
}