package de.netami.reportsystem.managers;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.Report;
import de.netami.reportsystem.types.ReportStatus;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AdminManager {
    
    private final NetamiReportSystemPlugin plugin;
    private final Map<UUID, ReportMode> playersInReportMode;
    
    public AdminManager(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
        this.playersInReportMode = new HashMap<>();
    }
    
    public void openAdminPanel(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 54, "§4§lReport Admin Panel");
        
        int totalReports = plugin.getReportManager().getTotalReports();
        int openReports = plugin.getReportManager().getOpenReportsCount();
        int inProgressReports = plugin.getReportManager().getInProgressReportsCount();
        int closedReports = plugin.getReportManager().getClosedReportsCount();
        
        ItemStack openItem = new ItemStack(Material.CHEST);
        ItemMeta openMeta = openItem.getItemMeta();
        openMeta.setDisplayName("§a📂 Open Reports (" + openReports + ")");
        List<String> openLore = new ArrayList<>();
        openLore.add("§7View all open reports that");
        openLore.add("§7need to be reviewed by admins.");
        openLore.add("");
        openLore.add("§eClick to view open reports");
        openMeta.setLore(openLore);
        openItem.setItemMeta(openMeta);
        inv.setItem(10, openItem);
        
        ItemStack progressItem = new ItemStack(Material.CLOCK);
        ItemMeta progressMeta = progressItem.getItemMeta();
        progressMeta.setDisplayName("§e⚠ In Progress Reports (" + inProgressReports + ")");
        List<String> progressLore = new ArrayList<>();
        progressLore.add("§7View reports that are currently");
        progressLore.add("§7being handled by administrators.");
        progressLore.add("");
        progressLore.add("§eClick to view in progress reports");
        progressMeta.setLore(progressLore);
        progressItem.setItemMeta(progressMeta);
        inv.setItem(12, progressItem);
        
        ItemStack closedItem = new ItemStack(Material.BARRIER);
        ItemMeta closedMeta = closedItem.getItemMeta();
        closedMeta.setDisplayName("§c❌ Closed Reports (" + closedReports + ")");
        List<String> closedLore = new ArrayList<>();
        closedLore.add("§7View reports that have been");
        closedLore.add("§7closed or resolved by admins.");
        closedLore.add("");
        closedLore.add("§eClick to view closed reports");
        closedMeta.setLore(closedLore);
        closedItem.setItemMeta(closedMeta);
        inv.setItem(14, closedItem);
        
        List<Report> myReports = plugin.getReportManager().getReportsByAssignedAdmin(admin.getUniqueId());
        ItemStack myReportsItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta myReportsMeta = myReportsItem.getItemMeta();
        myReportsMeta.setDisplayName("§b👤 My Reports (" + myReports.size() + ")");
        List<String> myReportsLore = new ArrayList<>();
        myReportsLore.add("§7View reports that are");
        myReportsLore.add("§7assigned to you.");
        myReportsLore.add("");
        myReportsLore.add("§eClick to view your reports");
        myReportsMeta.setLore(myReportsLore);
        myReportsItem.setItemMeta(myReportsMeta);
        inv.setItem(16, myReportsItem);
        
        List<Report> openReportsList = plugin.getReportManager().getOpenReports();
        
        int openUserReports = 0;
        int openBugReports = 0;
        int openMapBugReports = 0;
        
        for (Report report : openReportsList) {
            if (report.getType() != null) {
                switch (report.getType()) {
                    case USER:
                        openUserReports++;
                        break;
                    case BUG:
                        openBugReports++;
                        break;
                    case MAPBUG:
                        openMapBugReports++;
                        break;
                }
            }
        }
        
        ItemStack userReportsItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta userReportsMeta = userReportsItem.getItemMeta();
        userReportsMeta.setDisplayName("§c👤 User Reports (" + openUserReports + ")");
        List<String> userReportsLore = new ArrayList<>();
        userReportsLore.add("§7View all open user reports");
        userReportsLore.add("");
        userReportsLore.add("§eClick to view user reports");
        userReportsMeta.setLore(userReportsLore);
        userReportsItem.setItemMeta(userReportsMeta);
        inv.setItem(28, userReportsItem);
        
        ItemStack bugReportsItem = new ItemStack(Material.REDSTONE);
        ItemMeta bugReportsMeta = bugReportsItem.getItemMeta();
        bugReportsMeta.setDisplayName("§e🐛 Bug Reports (" + openBugReports + ")");
        List<String> bugReportsLore = new ArrayList<>();
        bugReportsLore.add("§7View all open bug reports");
        bugReportsLore.add("");
        bugReportsLore.add("§eClick to view bug reports");
        bugReportsMeta.setLore(bugReportsLore);
        bugReportsItem.setItemMeta(bugReportsMeta);
        inv.setItem(30, bugReportsItem);
        
        ItemStack mapBugReportsItem = new ItemStack(Material.MAP);
        ItemMeta mapBugReportsMeta = mapBugReportsItem.getItemMeta();
        mapBugReportsMeta.setDisplayName("§6🗺 Map Bug Reports (" + openMapBugReports + ")");
        List<String> mapBugReportsLore = new ArrayList<>();
        mapBugReportsLore.add("§7View all open map bug reports");
        mapBugReportsLore.add("");
        mapBugReportsLore.add("§eClick to view map bug reports");
        mapBugReportsMeta.setLore(mapBugReportsLore);
        mapBugReportsItem.setItemMeta(mapBugReportsMeta);
        inv.setItem(32, mapBugReportsItem);
        
        ItemStack statsItem = new ItemStack(Material.BOOK);
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.setDisplayName("§d📊 Statistics");
        List<String> statsLore = new ArrayList<>();
        statsLore.add("§7Total Reports: §f" + totalReports);
        statsLore.add("§7Open: §a" + openReports);
        statsLore.add("§7In Progress: §e" + inProgressReports);
        statsLore.add("§7Closed: §c" + closedReports);
        statsLore.add("");
        statsLore.add("§eClick for detailed statistics");
        statsMeta.setLore(statsLore);
        statsItem.setItemMeta(statsMeta);
        inv.setItem(49, statsItem);
        
        ItemStack resetItem = new ItemStack(Material.TNT);
        ItemMeta resetMeta = resetItem.getItemMeta();
        resetMeta.setDisplayName("§c🗑 Reset All Reports");
        List<String> resetLore = new ArrayList<>();
        resetLore.add("§7§l⚠ DANGER ZONE ⚠");
        resetLore.add("§7This will permanently delete");
        resetLore.add("§7ALL reports and reset statistics!");
        resetLore.add("");
        resetLore.add("§7This action cannot be undone!");
        resetLore.add("");
        resetLore.add("§c§lClick to reset all reports");
        resetMeta.setLore(resetLore);
        resetItem.setItemMeta(resetMeta);
        inv.setItem(53, resetItem);
        
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        
        admin.openInventory(inv);
    }
    
    public void openReportsList(Player admin, List<Report> reports, String title) {
        plugin.getLogger().info("Opening reports list for " + admin.getName() + " with title: " + title);
        plugin.getLogger().info("Reports count: " + reports.size());
        
        int size = 54;
        plugin.getLogger().info("Using fixed inventory size: " + size);
        
        Inventory inv = Bukkit.createInventory(null, size, title);
        plugin.getLogger().info("Created inventory with title: " + title);
        
        if (reports.isEmpty()) {
            ItemStack emptyItem = new ItemStack(Material.BARRIER);
            ItemMeta emptyMeta = emptyItem.getItemMeta();
            emptyMeta.setDisplayName("§c❌ No Reports Found");
            List<String> emptyLore = new ArrayList<>();
            emptyLore.add("§7There are no open reports");
            emptyLore.add("§7in this category.");
            emptyLore.add("");
            emptyLore.add("§7Check 'Closed Reports' to see");
            emptyLore.add("§7resolved reports of this type.");
            emptyMeta.setLore(emptyLore);
            emptyItem.setItemMeta(emptyMeta);
            inv.setItem(22, emptyItem);
        } else {
            int slot = 0;
            int maxSlots = 45;
            plugin.getLogger().info("Starting to iterate through " + reports.size() + " reports, max slots: " + maxSlots);
            
            for (Report report : reports) {
                plugin.getLogger().info("Loop iteration - slot: " + slot + ", maxSlots: " + maxSlots);
                
                if (slot >= maxSlots) {
                    plugin.getLogger().info("Breaking loop - slot " + slot + " >= " + maxSlots);
                    break;
                }
                
                if (report == null) {
                    plugin.getLogger().warning("Report is null at slot " + slot);
                    slot++;
                    continue;
                }
                
                plugin.getLogger().info("Processing report #" + report.getId() + " for slot " + slot);
                
                ItemStack item = getReportItem(report);
                if (item != null) {
                    inv.setItem(slot, item);
                    plugin.getLogger().info("Added report #" + report.getId() + " to slot " + slot + " - " + report.getTitle());
                } else {
                    plugin.getLogger().warning("Failed to create item for report #" + report.getId());
                }
                slot++;
            }
            
            plugin.getLogger().info("Finished processing reports. Final slot: " + slot);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§7← Back to Admin Panel");
        backItem.setItemMeta(backMeta);
        inv.setItem(inv.getSize() - 1, backItem);
        
        admin.openInventory(inv);
    }
    
    public void openReportDetails(Player admin, Report report) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Report #" + report.getId() + " Details");
        
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§e📋 Report Information");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7ID: §f#" + report.getId());
        infoLore.add("§7Type: " + report.getTypeDisplayName());
        infoLore.add("§7Status: " + report.getStatusDisplayName());
        infoLore.add("§7Title: §f" + report.getTitle());
        infoLore.add("");
        infoLore.add("§7Reporter: §f" + report.getReporterName());
        infoLore.add("§7Created: §f" + report.getFormattedCreatedAt());
        infoLore.add("§7Updated: §f" + report.getFormattedUpdatedAt());
        if (report.hasAssignedAdmin()) {
            infoLore.add("§7Assigned to: §f" + report.getAssignedAdminName());
        }
        if (report.getTargetPlayerName() != null) {
            infoLore.add("§7Target: §f" + report.getTargetPlayerName());
        }
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inv.setItem(4, infoItem);
        
        ItemStack descItem = new ItemStack(Material.BOOK);
        ItemMeta descMeta = descItem.getItemMeta();
        descMeta.setDisplayName("§b📖 Description");
        List<String> descLore = new ArrayList<>();
        descLore.add("§7" + report.getDescription());
        descMeta.setLore(descLore);
        descItem.setItemMeta(descMeta);
        inv.setItem(13, descItem);
        
        if (report.getLocation() != null) {
            ItemStack locItem = new ItemStack(Material.COMPASS);
            ItemMeta locMeta = locItem.getItemMeta();
            locMeta.setDisplayName("§a🧭 Location");
            List<String> locLore = new ArrayList<>();
            locLore.add("§7" + report.getLocationString());
            locLore.add("");
            locLore.add("§eClick to teleport");
            locMeta.setLore(locLore);
            locItem.setItemMeta(locMeta);
            inv.setItem(22, locItem);
        }
        
        if (!report.isClosed()) {
            if (!report.hasAssignedAdmin() || !report.getAssignedAdmin().equals(admin.getUniqueId())) {
                ItemStack assignItem = new ItemStack(Material.NAME_TAG);
                ItemMeta assignMeta = assignItem.getItemMeta();
                assignMeta.setDisplayName("§e👤 Assign to Me");
                List<String> assignLore = new ArrayList<>();
                assignLore.add("§7Take responsibility for this report");
                assignLore.add("");
                assignLore.add("§eClick to assign to yourself");
                assignMeta.setLore(assignLore);
                assignItem.setItemMeta(assignMeta);
                inv.setItem(37, assignItem);
            }
            
            ItemStack closeItem = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = closeItem.getItemMeta();
            closeMeta.setDisplayName("§c❌ Close Report");
            List<String> closeLore = new ArrayList<>();
            closeLore.add("§7Close this report with a response");
            closeLore.add("");
            closeLore.add("§eClick to close report");
            closeMeta.setLore(closeLore);
            closeItem.setItemMeta(closeMeta);
            inv.setItem(39, closeItem);
            
            ItemStack resolveItem = new ItemStack(Material.EMERALD);
            ItemMeta resolveMeta = resolveItem.getItemMeta();
            resolveMeta.setDisplayName("§a✅ Resolve Report");
            List<String> resolveLore = new ArrayList<>();
            resolveLore.add("§7Mark this report as resolved");
            resolveLore.add("");
            resolveLore.add("§eClick to resolve report");
            resolveMeta.setLore(resolveLore);
            resolveItem.setItemMeta(resolveMeta);
            inv.setItem(41, resolveItem);
        } else {
            ItemStack reopenItem = new ItemStack(Material.CHEST);
            ItemMeta reopenMeta = reopenItem.getItemMeta();
            reopenMeta.setDisplayName("§e📂 Reopen Report");
            List<String> reopenLore = new ArrayList<>();
            reopenLore.add("§7Reopen this closed report");
            reopenLore.add("");
            reopenLore.add("§eClick to reopen report");
            reopenMeta.setLore(reopenLore);
            reopenItem.setItemMeta(reopenMeta);
            inv.setItem(39, reopenItem);
        }
        
        if (report.hasResponse()) {
            ItemStack responseItem = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta responseMeta = responseItem.getItemMeta();
            responseMeta.setDisplayName("§d💬 Admin Response");
            List<String> responseLore = new ArrayList<>();
            responseLore.add("§7" + report.getAdminResponse());
            responseMeta.setLore(responseLore);
            responseItem.setItemMeta(responseMeta);
            inv.setItem(31, responseItem);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§7← Back");
        backItem.setItemMeta(backMeta);
        inv.setItem(45, backItem);
        
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        
        admin.openInventory(inv);
    }
    
    private ItemStack getReportItem(Report report) {
        plugin.getLogger().info("Creating item for report #" + report.getId() + " - Type: " + report.getType() + " - Title: " + report.getTitle());
        
        Material material;
        switch (report.getType()) {
            case USER:
                material = Material.PLAYER_HEAD;
                break;
            case BUG:
                material = Material.REDSTONE;
                break;
            case MAPBUG:
                material = Material.MAP;
                break;
            default:
                material = Material.PAPER;
        }
        
        plugin.getLogger().info("Using material: " + material.name() + " for report #" + report.getId());
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            plugin.getLogger().warning("ItemMeta is null for material: " + material.name());
            return null;
        }
        
        meta.setDisplayName("§e#" + report.getId() + " - " + report.getTitle());
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Type: " + report.getTypeDisplayName());
        lore.add("§7Status: " + report.getStatusDisplayName());
        lore.add("§7Reporter: §f" + report.getReporterName());
        lore.add("§7Created: §f" + report.getFormattedCreatedAt());
        if (report.hasAssignedAdmin()) {
            lore.add("§7Assigned to: §f" + report.getAssignedAdminName());
        }
        if (report.getTargetPlayerName() != null) {
            lore.add("§7Target: §f" + report.getTargetPlayerName());
        }
        lore.add("");
        lore.add("§eClick to view details");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        plugin.getLogger().info("Successfully created item for report #" + report.getId());
        return item;
    }
    
    public void handleAdminMenuClick(Player admin, String title, String itemName, ItemStack clickedItem) {
        plugin.getLogger().info("Admin menu click - Title: " + title + ", Item: " + itemName);
        
        if (title.equals("§4§lReport Admin Panel")) {
            handleMainAdminPanel(admin, itemName);
        } else if (title.contains("Details")) {
            handleReportDetails(admin, title, itemName);
        } else if (title.contains("Reports") || title.contains("📂") || title.contains("⚠") || title.contains("❌") || title.contains("👤") || title.contains("🐛") || title.contains("🗺")) {
            handleReportsList(admin, title, itemName, clickedItem);
        }
    }
    
    private void handleMainAdminPanel(Player admin, String itemName) {
        plugin.getLogger().info("Handling main admin panel click - Item: '" + itemName + "'");
        
        if (itemName.contains("Open Reports")) {
            plugin.getLogger().info("Opening open reports");
            openReportsList(admin, plugin.getReportManager().getOpenReports(), "§a📂 Open Reports");
        } else if (itemName.contains("In Progress Reports")) {
            plugin.getLogger().info("Opening in progress reports");
            openReportsList(admin, plugin.getReportManager().getInProgressReports(), "§e⚠ In Progress Reports");
        } else if (itemName.contains("Closed Reports")) {
            plugin.getLogger().info("Opening closed reports");
            openReportsList(admin, plugin.getReportManager().getClosedReports(), "§c❌ Closed Reports");
        } else if (itemName.contains("My Reports")) {
            plugin.getLogger().info("Opening my reports");
            openReportsList(admin, plugin.getReportManager().getReportsByAssignedAdmin(admin.getUniqueId()), "§b👤 My Reports");
        } else if (itemName.contains("Map Bug Reports")) {
            plugin.getLogger().info("Opening map bug reports");
            
            List<Report> allReports = plugin.getReportManager().getOpenReports();
            List<Report> mapBugReports = new ArrayList<>();
            
            plugin.getLogger().info("Filtering from " + allReports.size() + " open reports");
            for (Report report : allReports) {
                plugin.getLogger().info("Checking report #" + report.getId() + " - Title: " + report.getTitle());
                plugin.getLogger().info("  Type: " + report.getType());
                plugin.getLogger().info("  TypeDisplayName: " + report.getTypeDisplayName());
                
                boolean isMapBug = false;
                
                if (report.getType() != null) {
                    String typeName = report.getType().name();
                    plugin.getLogger().info("  TypeName: " + typeName);
                    
                    if (typeName.equals("MAPBUG") || 
                        report.getTypeDisplayName().contains("Map Bug") ||
                        report.getTitle().toLowerCase().contains("map")) {
                        isMapBug = true;
                    }
                }
                
                if (isMapBug) {
                    mapBugReports.add(report);
                    plugin.getLogger().info("  -> ADDED to map bug reports list");
                } else {
                    plugin.getLogger().info("  -> NOT a map bug report");
                }
            }
            
            plugin.getLogger().info("Found " + mapBugReports.size() + " open map bug reports after filtering");
            
            openReportsList(admin, mapBugReports, "§6🗺 Map Bug Reports");
        } else if (itemName.contains("User Reports")) {
            plugin.getLogger().info("Opening user reports");
            
            List<Report> allReports = plugin.getReportManager().getOpenReports();
            List<Report> userReports = new ArrayList<>();
            
            for (Report report : allReports) {
                if (report.getType() != null && report.getType().name().equals("USER")) {
                    userReports.add(report);
                }
            }
            
            plugin.getLogger().info("Found " + userReports.size() + " open user reports after manual filtering");
            
            openReportsList(admin, userReports, "§c👤 User Reports");
        } else if (itemName.contains("Bug Reports") && !itemName.contains("Map Bug Reports")) {
            plugin.getLogger().info("Opening bug reports");
            
            List<Report> allReports = plugin.getReportManager().getOpenReports();
            List<Report> bugReports = new ArrayList<>();
            
            for (Report report : allReports) {
                if (report.getType() != null && report.getType().name().equals("BUG")) {
                    bugReports.add(report);
                }
            }
            
            plugin.getLogger().info("Found " + bugReports.size() + " open bug reports after manual filtering");
            
            openReportsList(admin, bugReports, "§e🐛 Bug Reports");
        } else if (itemName.contains("Reset All Reports")) {
            plugin.getLogger().info("Starting reset process");
            startResetProcess(admin);
        } else {
            plugin.getLogger().warning("Unknown admin panel item clicked: '" + itemName + "'");
        }
    }
    
    private void handleReportDetails(Player admin, String title, String itemName) {
        String idStr = title.split("#")[1].split(" ")[0];
        int reportId;
        try {
            reportId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            admin.sendMessage("§c[Reports] Invalid report ID format!");
            return;
        }
        
        Report report = plugin.getReportManager().getReport(reportId);
        
        if (report == null) {
            admin.sendMessage("§c[Reports] Report not found!");
            openAdminPanel(admin);
            return;
        }
        
        if (itemName.equals("§e👤 Assign to Me")) {
            plugin.getReportManager().assignReport(reportId, admin);
            admin.sendMessage("§a[Reports] Report #" + reportId + " assigned to you!");
            Report updatedReport = plugin.getReportManager().getReport(reportId);
            openReportDetails(admin, updatedReport);
        } else if (itemName.equals("§c❌ Close Report")) {
            startResponseProcess(admin, reportId, "close");
        } else if (itemName.equals("§a✅ Resolve Report")) {
            startResponseProcess(admin, reportId, "resolve");
        } else if (itemName.equals("§e📂 Reopen Report")) {
            plugin.getReportManager().reopenReport(reportId, admin);
            admin.sendMessage("§a[Reports] Report #" + reportId + " reopened!");
            Report updatedReport = plugin.getReportManager().getReport(reportId);
            openReportDetails(admin, updatedReport);
        } else if (itemName.contains("Location") || itemName.contains("🧭")) {
            plugin.getReportManager().teleportToReport(admin, reportId);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                openReportDetails(admin, report);
            }, 5L);
        } else if (itemName.equals("§7← Back")) {
            openAdminPanel(admin);
        }
    }
    
    private void handleReportsList(Player admin, String title, String itemName, ItemStack clickedItem) {
        if (itemName.equals("§7← Back to Admin Panel")) {
            openAdminPanel(admin);
            return;
        }
        
        if (itemName.startsWith("§e#")) {
            String idStr = itemName.split("#")[1].split(" ")[0];
            try {
                int reportId = Integer.parseInt(idStr);
                Report report = plugin.getReportManager().getReport(reportId);
                if (report != null) {
                    openReportDetails(admin, report);
                }
            } catch (NumberFormatException e) {
                admin.sendMessage("§c[Reports] Invalid report ID!");
            }
        }
    }
    
    private void startResponseProcess(Player admin, int reportId, String action) {
        admin.closeInventory();
        admin.sendMessage("§e[Reports] Please type your response for report #" + reportId + ":");
        admin.sendMessage("§7Type 'cancel' to cancel the process.");
        
        playersInReportMode.put(admin.getUniqueId(), new ReportMode(reportId, action));
    }
    
    public void setPlayerInReportMode(Player player, ReportType type, Player targetPlayer, boolean isCustomTitle) {
        plugin.getLogger().info("Setting player " + player.getName() + " in report mode - Type: " + type + ", Target: " + (targetPlayer != null ? targetPlayer.getName() : "null") + ", CustomTitle: " + isCustomTitle);
        playersInReportMode.put(player.getUniqueId(), new ReportMode(type, targetPlayer, isCustomTitle));
        plugin.getLogger().info("Player " + player.getName() + " is now in report mode. Total players in report mode: " + playersInReportMode.size());
    }
    
    public ReportMode getPlayerReportMode(Player player) {
        return playersInReportMode.get(player.getUniqueId());
    }
    
    public void removePlayerFromReportMode(Player player) {
        playersInReportMode.remove(player.getUniqueId());
    }
    
    public boolean isPlayerInReportMode(Player player) {
        return playersInReportMode.containsKey(player.getUniqueId());
    }
    
    private void startResetProcess(Player admin) {
        admin.closeInventory();
        admin.sendMessage("§c§l[DANGER] You are about to delete ALL reports!");
        admin.sendMessage("§7This will permanently delete all reports and reset statistics.");
        admin.sendMessage("§7This action cannot be undone!");
        admin.sendMessage("");
        admin.sendMessage("§eType 'CONFIRM RESET' to proceed or 'cancel' to abort:");
        
        playersInReportMode.put(admin.getUniqueId(), new ReportMode(-999, "reset"));
    }
    
    public void executeResetAllReports(Player admin) {
        int totalReports = plugin.getReportManager().getTotalReports();
        
        plugin.getReportManager().resetAllReports();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("reports.notify")) {
                player.sendMessage("§c§l[REPORTS] ALL REPORTS HAVE BEEN RESET!");
                player.sendMessage("§7" + admin.getName() + " has deleted all " + totalReports + " reports.");
                player.sendMessage("§7Statistics have been reset to zero.");
            }
        }
        
        plugin.getLogger().warning(admin.getName() + " has reset all reports! " + totalReports + " reports were deleted.");
        
        refreshAdminUIs();
        
        admin.sendMessage("§a[Reports] Successfully reset all reports!");
        admin.sendMessage("§7" + totalReports + " reports were deleted and statistics reset.");
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            openAdminPanel(admin);
        }, 20L);
    }
    
    public void refreshAdminUIs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("reports.admin") && player.getOpenInventory() != null) {
                String title = player.getOpenInventory().getTitle();
                if (title != null && (title.contains("Report") || title.contains("Admin Panel"))) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (title.equals("§4§lReport Admin Panel")) {
                            openAdminPanel(player);
                        } else if (title.contains("Details")) {
                            try {
                                String idStr = title.split("#")[1].split(" ")[0];
                                int reportId = Integer.parseInt(idStr);
                                Report report = plugin.getReportManager().getReport(reportId);
                                if (report != null) {
                                    openReportDetails(player, report);
                                } else {
                                    openAdminPanel(player);
                                }
                            } catch (Exception e) {
                                openAdminPanel(player);
                            }
                        } else if (title.contains("Open Reports")) {
                            openReportsList(player, plugin.getReportManager().getOpenReports(), "§a📂 Open Reports");
                        } else if (title.contains("In Progress Reports")) {
                            openReportsList(player, plugin.getReportManager().getInProgressReports(), "§e⚠ In Progress Reports");
                        } else if (title.contains("Closed Reports")) {
                            openReportsList(player, plugin.getReportManager().getClosedReports(), "§c❌ Closed Reports");
                        } else if (title.contains("My Reports")) {
                            openReportsList(player, plugin.getReportManager().getReportsByAssignedAdmin(player.getUniqueId()), "§b👤 My Reports");
                        } else if (title.contains("User Reports")) {
                            openReportsList(player, plugin.getReportManager().getReportsByType(ReportType.USER), "§c👤 User Reports");
                        } else if (title.contains("Bug Reports") && !title.contains("Map")) {
                            openReportsList(player, plugin.getReportManager().getReportsByType(ReportType.BUG), "§e🐛 Bug Reports");
                        } else if (title.contains("Map Bug Reports")) {
                            openReportsList(player, plugin.getReportManager().getReportsByType(ReportType.MAPBUG), "§6🗺 Map Bug Reports");
                        }
                    }, 2L);
                }
            }
        }
    }
    
    public void notifyAdminsOfUpdate(Report report, String action, Player admin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("reports.notify") && !player.equals(admin)) {
                player.sendMessage("§b[Reports] " + admin.getName() + " " + action + " report #" + report.getId());
                player.sendMessage("§7Type: " + report.getTypeDisplayName() + " | Title: " + report.getTitle());
            }
        }
    }
    
    public static class ReportMode {
        private final ReportType type;
        private final Player targetPlayer;
        private final boolean isCustomTitle;
        private final int reportId;
        private final String action;
        private String title;
        
        public ReportMode(ReportType type, Player targetPlayer, boolean isCustomTitle) {
            this.type = type;
            this.targetPlayer = targetPlayer;
            this.isCustomTitle = isCustomTitle;
            this.reportId = -1;
            this.action = null;
            this.title = null;
        }
        
        public ReportMode(int reportId, String action) {
            this.reportId = reportId;
            this.action = action;
            this.type = null;
            this.targetPlayer = null;
            this.isCustomTitle = false;
            this.title = null;
        }
        
        public ReportType getType() { return type; }
        public Player getTargetPlayer() { return targetPlayer; }
        public boolean isCustomTitle() { return isCustomTitle; }
        public int getReportId() { return reportId; }
        public String getAction() { return action; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public boolean isAdminResponse() { return reportId != -1; }
    }
}