package de.netami.reportsystem;

import de.netami.reportsystem.commands.*;
import de.netami.reportsystem.listeners.*;
import de.netami.reportsystem.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class NetamiReportSystemPlugin extends JavaPlugin {
    
    private static NetamiReportSystemPlugin instance;
    
    private ReportManager reportManager;
    private DatabaseManager databaseManager;
    private NotificationManager notificationManager;
    private AdminManager adminManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("NetamiReportSystem has been enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("Report categories: User, Bug, MapBug");
    }
    
    @Override
    public void onDisable() {
        // Save all pending reports
        if (databaseManager != null) {
            databaseManager.saveAllReports();
        }
        
        // Cancel notification tasks
        if (notificationManager != null) {
            notificationManager.cleanup();
        }
        
        getLogger().info("NetamiReportSystem has been disabled!");
    }
    
    private void initializeManagers() {
        databaseManager = new DatabaseManager(this);
        reportManager = new ReportManager(this);
        notificationManager = new NotificationManager(this);
        adminManager = new AdminManager(this);
    }
    
    private void registerCommands() {
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("reportadmin").setExecutor(new ReportAdminCommand(this));
        getCommand("reportstats").setExecutor(new ReportStatsCommand(this));
        getCommand("reporttp").setExecutor(new ReportTeleportCommand(this));
        getCommand("reportreload").setExecutor(new ReportReloadCommand(this));
        getCommand("reporttest").setExecutor(new ReportTestCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
    }
    
    public static NetamiReportSystemPlugin getInstance() {
        return instance;
    }
    
    // Getters for managers
    public ReportManager getReportManager() {
        return reportManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
    
    public AdminManager getAdminManager() {
        return adminManager;
    }
}