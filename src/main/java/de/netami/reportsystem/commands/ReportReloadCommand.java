package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ReportReloadCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportReloadCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reports.reload")) {
            sender.sendMessage("§c[Reports] You don't have permission to reload the plugin!");
            return true;
        }
        
        sender.sendMessage("§e[Reports] Reloading plugin...");
        
        plugin.reloadConfig();
        
        plugin.getDatabaseManager().reloadData();
        
        List<Player> admins = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("reports.admin"))
                .collect(Collectors.toList());
        
        for (Player admin : admins) {
            plugin.getAdminManager().refreshAdminUIs();
        }
        
        sender.sendMessage("§a[Reports] Plugin reloaded successfully!");
        plugin.getLogger().info("Plugin reloaded by " + sender.getName());
        
        return true;
    }
}