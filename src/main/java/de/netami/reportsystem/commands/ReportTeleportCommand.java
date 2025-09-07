package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportTeleportCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportTeleportCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("reports.teleport")) {
            player.sendMessage("§cYou don't have permission to teleport to reports!");
            return true;
        }
        
        if (args.length != 1) {
            player.sendMessage("§cUsage: /reporttp <report_id>");
            return true;
        }
        
        try {
            int reportId = Integer.parseInt(args[0]);
            plugin.getReportManager().teleportToReport(player, reportId);
        } catch (NumberFormatException e) {
            player.sendMessage("§c[Reports] Invalid report ID! Please use a number.");
        }
        
        return true;
    }
}