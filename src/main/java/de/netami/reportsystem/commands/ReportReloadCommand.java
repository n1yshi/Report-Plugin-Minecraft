package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReportReloadCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportReloadCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reports.admin")) {
            sender.sendMessage("§cYou don't have permission to reload the plugin!");
            return true;
        }
        
        // Reload configuration
        plugin.reloadConfig();
        
        // Reload database
        plugin.getDatabaseManager().reloadData();
        
        sender.sendMessage("§a[Reports] Plugin configuration and data reloaded successfully!");
        plugin.getLogger().info("Plugin reloaded by " + sender.getName());
        
        return true;
    }
}