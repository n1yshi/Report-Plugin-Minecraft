package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportAdminCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportAdminCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("reports.admin")) {
            player.sendMessage("§cYou don't have permission to access the admin panel!");
            return true;
        }
        
        plugin.getAdminManager().openAdminPanel(player);
        return true;
    }
}