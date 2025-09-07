package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportTestCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportTestCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("reports.test")) {
            player.sendMessage("§cYou don't have permission to use test commands!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage("§e[ReportTest] Available test commands:");
            player.sendMessage("§7/reporttest mapbug - Test map bug report creation");
            player.sendMessage("§7/reporttest bug - Test bug report creation");
            player.sendMessage("§7/reporttest all - Test all report types");
            player.sendMessage("§7/reporttest mode - Check if you're in report mode");
            player.sendMessage("§7/reporttest clear - Clear your report mode");
            player.sendMessage("§7/reporttest stats - Show report statistics");
            player.sendMessage("§7/reporttest menu - Open report menu directly");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "mapbug":
                testMapBugReport(player);
                break;
            case "bug":
                testBugReport(player);
                break;
            case "all":
                testAllReportTypes(player);
                break;
            case "mode":
                checkReportMode(player);
                break;
            case "clear":
                clearReportMode(player);
                break;
            case "stats":
                showStats(player);
                break;
            case "menu":
                openReportMenu(player);
                break;
            default:
                player.sendMessage("§c[ReportTest] Unknown subcommand: " + subCommand);
                break;
        }
        
        return true;
    }
    
    private void testMapBugReport(Player player) {
        player.sendMessage("§e[ReportTest] Creating test map bug report...");
        
        try {
            plugin.getReportManager().createMapBugReport(player, "Test Map Bug", "This is a test map bug report created via command");
            player.sendMessage("§a[ReportTest] Map bug report created successfully!");
        } catch (Exception e) {
            player.sendMessage("§c[ReportTest] Error creating map bug report: " + e.getMessage());
            plugin.getLogger().severe("Error in test map bug report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testBugReport(Player player) {
        player.sendMessage("§e[ReportTest] Creating test bug report...");
        
        try {
            plugin.getReportManager().createBugReport(player, "Test Bug", "This is a test bug report created via command");
            player.sendMessage("§a[ReportTest] Bug report created successfully!");
        } catch (Exception e) {
            player.sendMessage("§c[ReportTest] Error creating bug report: " + e.getMessage());
            plugin.getLogger().severe("Error in test bug report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void checkReportMode(Player player) {
        boolean inMode = plugin.getAdminManager().isPlayerInReportMode(player);
        player.sendMessage("§e[ReportTest] Report mode status: " + (inMode ? "§aIN REPORT MODE" : "§cNOT IN REPORT MODE"));
        
        if (inMode) {
            var mode = plugin.getAdminManager().getPlayerReportMode(player);
            if (mode != null) {
                player.sendMessage("§7Type: " + mode.getType());
                player.sendMessage("§7Custom Title: " + mode.isCustomTitle());
                player.sendMessage("§7Title: " + mode.getTitle());
                player.sendMessage("§7Target: " + (mode.getTargetPlayer() != null ? mode.getTargetPlayer().getName() : "null"));
                player.sendMessage("§7Admin Response: " + mode.isAdminResponse());
            }
        }
    }
    
    private void clearReportMode(Player player) {
        if (plugin.getAdminManager().isPlayerInReportMode(player)) {
            plugin.getAdminManager().removePlayerFromReportMode(player);
            player.sendMessage("§a[ReportTest] Report mode cleared!");
        } else {
            player.sendMessage("§c[ReportTest] You are not in report mode!");
        }
    }
    
    private void testAllReportTypes(Player player) {
        player.sendMessage("§e[ReportTest] Testing all report types...");
        
        try {
            // Test Map Bug Report
            plugin.getReportManager().createMapBugReport(player, "Test Map Bug", "Test map bug description");
            player.sendMessage("§a[ReportTest] ✓ Map bug report created");
            
            // Test Bug Report
            plugin.getReportManager().createBugReport(player, "Test Bug", "Test bug description");
            player.sendMessage("§a[ReportTest] ✓ Bug report created");
            
            player.sendMessage("§a[ReportTest] All report types tested successfully!");
            
        } catch (Exception e) {
            player.sendMessage("§c[ReportTest] Error testing report types: " + e.getMessage());
            plugin.getLogger().severe("Error in test all report types: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void openReportMenu(Player player) {
        player.sendMessage("§e[ReportTest] Opening report menu...");
        
        try {
            // Get the report command and open the menu
            de.netami.reportsystem.commands.ReportCommand reportCommand = new de.netami.reportsystem.commands.ReportCommand(plugin);
            reportCommand.openReportMenu(player);
            player.sendMessage("§a[ReportTest] Report menu opened!");
        } catch (Exception e) {
            player.sendMessage("§c[ReportTest] Error opening report menu: " + e.getMessage());
            plugin.getLogger().severe("Error opening report menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showStats(Player player) {
        player.sendMessage("§e[ReportTest] Report Statistics:");
        player.sendMessage("§7Total Reports: " + plugin.getReportManager().getTotalReports());
        player.sendMessage("§7Open Reports: " + plugin.getReportManager().getOpenReportsCount());
        player.sendMessage("§7In Progress: " + plugin.getReportManager().getInProgressReportsCount());
        player.sendMessage("§7Closed Reports: " + plugin.getReportManager().getClosedReportsCount());
        player.sendMessage("§7Map Bug Reports: " + plugin.getReportManager().getReportsCountByType(ReportType.MAPBUG));
        player.sendMessage("§7Bug Reports: " + plugin.getReportManager().getReportsCountByType(ReportType.BUG));
        player.sendMessage("§7User Reports: " + plugin.getReportManager().getReportsCountByType(ReportType.USER));
    }
}