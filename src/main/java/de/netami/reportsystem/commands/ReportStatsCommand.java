package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.ReportStatus;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ReportStatsCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportStatsCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reports.admin")) {
            sender.sendMessage("§cYou don't have permission to view report statistics!");
            return true;
        }
        
        // Get statistics
        int totalReports = plugin.getReportManager().getTotalReports();
        Map<ReportStatus, Integer> statusCounts = plugin.getReportManager().getReportCountsByStatus();
        Map<ReportType, Integer> typeCounts = plugin.getReportManager().getReportCountsByType();
        
        // Send statistics
        sender.sendMessage("§6§l=== Report System Statistics ===");
        sender.sendMessage("§7Total Reports: §f" + totalReports);
        sender.sendMessage("");
        
        // Status breakdown
        sender.sendMessage("§e§lBy Status:");
        for (ReportStatus status : ReportStatus.values()) {
            int count = statusCounts.getOrDefault(status, 0);
            double percentage = totalReports > 0 ? (count * 100.0 / totalReports) : 0;
            sender.sendMessage(String.format("  %s §f%d §7(%.1f%%)", 
                status.getColoredDisplayName(), count, percentage));
        }
        sender.sendMessage("");
        
        // Type breakdown
        sender.sendMessage("§e§lBy Type:");
        for (ReportType type : ReportType.values()) {
            int count = typeCounts.getOrDefault(type, 0);
            double percentage = totalReports > 0 ? (count * 100.0 / totalReports) : 0;
            String displayName = getTypeDisplayName(type);
            sender.sendMessage(String.format("  %s §f%d §7(%.1f%%)", 
                displayName, count, percentage));
        }
        
        // Personal statistics for players
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int myReports = plugin.getReportManager().getReportsByAssignedAdmin(player.getUniqueId()).size();
            sender.sendMessage("");
            sender.sendMessage("§b§lYour Statistics:");
            sender.sendMessage("§7Reports assigned to you: §f" + myReports);
        }
        
        return true;
    }
    
    private String getTypeDisplayName(ReportType type) {
        switch (type) {
            case USER:
                return "§c👤 User Reports";
            case BUG:
                return "§e🐛 Bug Reports";
            case MAPBUG:
                return "§6🗺 Map Bug Reports";
            default:
                return "§7❓ Unknown";
        }
    }
}