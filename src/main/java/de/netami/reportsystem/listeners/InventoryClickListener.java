package de.netami.reportsystem.listeners;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.commands.ReportCommand;
import de.netami.reportsystem.managers.AdminManager;
import de.netami.reportsystem.types.Report;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryClickListener implements Listener {
    
    private final NetamiReportSystemPlugin plugin;
    private final ReportCommand reportCommand;
    private final AdminManager adminManager;
    
    // Store temporary report data
    private final Map<UUID, ReportType> pendingReportTypes = new HashMap<>();
    private final Map<UUID, Player> pendingTargetPlayers = new HashMap<>();
    private final Map<UUID, String> pendingReportTitles = new HashMap<>();
    
    public InventoryClickListener(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
        this.reportCommand = new ReportCommand(plugin);
        this.adminManager = plugin.getAdminManager();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Debug logging for all inventory clicks
        plugin.getLogger().info("Inventory click - Player: " + player.getName() + ", Title: '" + title + "'");
        
        // Check if it's a report-related inventory
        if (!title.contains("Report") && !title.contains("Admin Panel") && !title.contains("Details") && !title.contains("Select Player") && !title.startsWith("§e📝")) {
            plugin.getLogger().info("Not a report-related inventory, ignoring");
            return;
        }
        
        // ALWAYS cancel the event to prevent item taking
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            plugin.getLogger().info("Clicked item is null or has no meta, ignoring");
            return;
        }
        
        String itemName = clickedItem.getItemMeta().getDisplayName();
        plugin.getLogger().info("Clicked item name: '" + itemName + "'");
        
        // Ignore clicks on glass panes and empty display names
        if (itemName == null || itemName.trim().isEmpty() || itemName.equals(" ")) {
            plugin.getLogger().info("Item name is null/empty/space, ignoring");
            return;
        }
        
        // Main report menu
        if (title.equals("§c§lReport System")) {
            plugin.getLogger().info("Handling main report menu");
            handleMainReportMenu(player, itemName);
        }
        // Player selection menu
        else if (title.equals("§c��� Select Player to Report")) {
            plugin.getLogger().info("Handling player selection menu");
            handlePlayerSelection(player, itemName, clickedItem);
        }
        // Report type menus
        else if (title.startsWith("§e📝")) {
            plugin.getLogger().info("Handling report type menu");
            handleReportTypeMenu(player, itemName, title);
        }
        // Admin panel menus - handle all admin-related inventories
        else if (title.contains("Admin Panel") || title.contains("Reports") || title.contains("Details")) {
            plugin.getLogger().info("Handling admin menu");
            adminManager.handleAdminMenuClick(player, title, itemName, clickedItem);
        } else {
            plugin.getLogger().warning("Unknown inventory title: '" + title + "'");
        }
    }
    
    private void handleMainReportMenu(Player player, String itemName) {
        plugin.getLogger().info("Main report menu click - Item: '" + itemName + "'");
        
        switch (itemName) {
            case "§c👤 Report User":
                plugin.getLogger().info("User report selected");
                if (!player.hasPermission("reports.user")) {
                    player.sendMessage("§c[Reports] You don't have permission to report users!");
                    return;
                }
                pendingReportTypes.put(player.getUniqueId(), ReportType.USER);
                reportCommand.openUserReportMenu(player);
                break;
                
            case "§e🐛 Report Bug":
                plugin.getLogger().info("Bug report selected");
                if (!player.hasPermission("reports.bug")) {
                    player.sendMessage("§c[Reports] You don't have permission to report bugs!");
                    return;
                }
                pendingReportTypes.put(player.getUniqueId(), ReportType.BUG);
                reportCommand.openReportTypeMenu(player, ReportType.BUG, null);
                break;
                
            case "§6🗺 Report Map Bug":
                plugin.getLogger().info("Map bug report selected");
                if (!player.hasPermission("reports.mapbug")) {
                    player.sendMessage("§c[Reports] You don't have permission to report map bugs!");
                    plugin.getLogger().warning("Player " + player.getName() + " doesn't have reports.mapbug permission");
                    return;
                }
                plugin.getLogger().info("Permission check passed, storing pending report type");
                pendingReportTypes.put(player.getUniqueId(), ReportType.MAPBUG);
                plugin.getLogger().info("Calling openReportTypeMenu for MAPBUG");
                reportCommand.openReportTypeMenu(player, ReportType.MAPBUG, null);
                plugin.getLogger().info("openReportTypeMenu call completed");
                break;
                
            default:
                plugin.getLogger().warning("Unknown item clicked in main report menu: '" + itemName + "'");
                break;
        }
    }
    
    private void handlePlayerSelection(Player player, String itemName, ItemStack clickedItem) {
        if (itemName.equals("§7← Back to Report Menu")) {
            reportCommand.openReportMenu(player);
            return;
        }
        
        // Extract player name from the item
        if (clickedItem.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                Player targetPlayer = meta.getOwningPlayer().getPlayer();
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    pendingTargetPlayers.put(player.getUniqueId(), targetPlayer);
                    reportCommand.openReportTypeMenu(player, ReportType.USER, targetPlayer);
                } else {
                    player.sendMessage("§c[Reports] That player is no longer online!");
                    reportCommand.openUserReportMenu(player);
                }
            }
        }
    }
    
    private void handleReportTypeMenu(Player player, String itemName, String title) {
        plugin.getLogger().info("Handling report type menu - Player: " + player.getName() + ", Item: " + itemName + ", Title: " + title);
        
        ReportType type = pendingReportTypes.get(player.getUniqueId());
        if (type == null) {
            plugin.getLogger().warning("Report type not found for player " + player.getName());
            player.sendMessage("§c[Reports] Error: Report type not found!");
            player.closeInventory();
            return;
        }
        
        plugin.getLogger().info("Found pending report type: " + type + " for player " + player.getName());
        
        if (itemName.equals("§7← Back to Report Menu")) {
            plugin.getLogger().info("Player " + player.getName() + " clicked back to report menu");
            reportCommand.openReportMenu(player);
            return;
        } else if (itemName.equals("§7← Back to Player Selection")) {
            plugin.getLogger().info("Player " + player.getName() + " clicked back to player selection");
            reportCommand.openUserReportMenu(player);
            return;
        } else if (itemName.equals("§b✏ Custom Report")) {
            plugin.getLogger().info("Player " + player.getName() + " clicked custom report");
            // Start custom report process
            startCustomReportProcess(player, type);
            return;
        }
        
        // Handle predefined report reasons
        if (itemName.startsWith("§e")) {
            String reportTitle = itemName.substring(2); // Remove color code
            plugin.getLogger().info("Player " + player.getName() + " selected predefined reason: " + reportTitle);
            pendingReportTitles.put(player.getUniqueId(), reportTitle);
            startReportDescriptionProcess(player, type, reportTitle);
        } else {
            plugin.getLogger().warning("Unknown item clicked in report type menu by " + player.getName() + ": " + itemName);
        }
    }
    
    private void startCustomReportProcess(Player player, ReportType type) {
        player.closeInventory();
        player.sendMessage("§e[Reports] Please type your custom report title in chat:");
        player.sendMessage("§7Type 'cancel' to cancel the report process.");
        
        // Only get target player for USER reports
        Player targetPlayer = null;
        if (type == ReportType.USER) {
            targetPlayer = pendingTargetPlayers.get(player.getUniqueId());
        }
        
        // Set player in custom report mode
        plugin.getAdminManager().setPlayerInReportMode(player, type, targetPlayer, true);
    }
    
    private void startReportDescriptionProcess(Player player, ReportType type, String title) {
        plugin.getLogger().info("Starting report description process for " + player.getName() + " - Type: " + type + ", Title: " + title);
        
        player.closeInventory();
        player.sendMessage("§e[Reports] Report Title: §f" + title);
        player.sendMessage("§e[Reports] Please provide a detailed description in chat:");
        player.sendMessage("§7Type 'cancel' to cancel the report process.");
        
        // Only get target player for USER reports
        Player targetPlayer = null;
        if (type == ReportType.USER) {
            targetPlayer = pendingTargetPlayers.get(player.getUniqueId());
            plugin.getLogger().info("Target player for user report: " + (targetPlayer != null ? targetPlayer.getName() : "null"));
        }
        
        // Set player in report mode with the title already set
        plugin.getAdminManager().setPlayerInReportMode(player, type, targetPlayer, false);
        
        // Store the title separately for the chat listener to use
        AdminManager.ReportMode mode = plugin.getAdminManager().getPlayerReportMode(player);
        if (mode != null) {
            mode.setTitle(title);
            plugin.getLogger().info("Set title '" + title + "' for player " + player.getName() + " in report mode");
        } else {
            plugin.getLogger().warning("Failed to get report mode for player " + player.getName() + " after setting it");
        }
    }
    
    private void createReportFromMenu(Player reporter, ReportType type, String title, String description, Player targetPlayer) {
        Report report;
        
        switch (type) {
            case USER:
                if (targetPlayer == null) {
                    reporter.sendMessage("§c[Reports] Error: Target player not found!");
                    return;
                }
                report = plugin.getReportManager().createUserReport(reporter, title, description, targetPlayer);
                break;
                
            case BUG:
                report = plugin.getReportManager().createBugReport(reporter, title, description);
                break;
                
            case MAPBUG:
                report = plugin.getReportManager().createMapBugReport(reporter, title, description);
                break;
                
            default:
                reporter.sendMessage("§c[Reports] Error: Invalid report type!");
                return;
        }
        
        // Clean up temporary data
        pendingReportTypes.remove(reporter.getUniqueId());
        pendingTargetPlayers.remove(reporter.getUniqueId());
        pendingReportTitles.remove(reporter.getUniqueId());
        
        // Send confirmation with enhanced UI feedback
        reporter.sendMessage("§a[Reports] Your report has been submitted successfully!");
        reporter.sendMessage("§7Report ID: #" + report.getId());
        reporter.sendMessage("§7Type: " + report.getTypeDisplayName());
        reporter.sendMessage("§7Location: " + report.getLocationString());
        reporter.sendMessage("§7You will be notified when an admin reviews your report.");
        
        // Play success sound
        reporter.playSound(reporter.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        
        // Show success title
        reporter.sendTitle("§a§lReport Submitted", "§7ID: #" + report.getId(), 10, 40, 10);
    }
    
    public ReportType getPendingReportType(Player player) {
        return pendingReportTypes.get(player.getUniqueId());
    }
    
    public Player getPendingTargetPlayer(Player player) {
        return pendingTargetPlayers.get(player.getUniqueId());
    }
    
    public String getPendingReportTitle(Player player) {
        return pendingReportTitles.get(player.getUniqueId());
    }
    
    public void clearPendingData(Player player) {
        pendingReportTypes.remove(player.getUniqueId());
        pendingTargetPlayers.remove(player.getUniqueId());
        pendingReportTitles.remove(player.getUniqueId());
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        String title = event.getView().getTitle();
        
        // Check if it's a report-related inventory
        if (title.contains("Report") || title.contains("Admin Panel") || title.contains("Details") || title.contains("Select Player") || title.startsWith("§e📝")) {
            // ALWAYS cancel drag events to prevent item manipulation
            event.setCancelled(true);
        }
    }
}