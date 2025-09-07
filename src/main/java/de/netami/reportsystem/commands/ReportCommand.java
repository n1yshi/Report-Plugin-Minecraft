package de.netami.reportsystem.commands;

import de.netami.reportsystem.NetamiReportSystemPlugin;
import de.netami.reportsystem.types.ReportType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ReportCommand implements CommandExecutor {
    
    private final NetamiReportSystemPlugin plugin;
    
    public ReportCommand(NetamiReportSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("reports.use")) {
            player.sendMessage("§cYou don't have permission to create reports!");
            return true;
        }
        
        if (!plugin.getReportManager().canPlayerReport(player)) {
            long remaining = plugin.getReportManager().getRemainingCooldown(player);
            player.sendMessage("§c[Reports] You must wait " + remaining + " seconds before creating another report!");
            return true;
        }
        
        openReportMenu(player);
        return true;
    }
    
    public void openReportMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§c§lReport System");
        
        ItemStack userReport = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta userMeta = (SkullMeta) userReport.getItemMeta();
        userMeta.setDisplayName("§c👤 Report User");
        List<String> userLore = new ArrayList<>();
        userLore.add("§7Report a player for misconduct");
        userLore.add("§7such as cheating, griefing, or");
        userLore.add("§7inappropriate behavior.");
        userLore.add("");
        userLore.add("§eClick to create a user report");
        userMeta.setLore(userLore);
        userReport.setItemMeta(userMeta);
        inv.setItem(11, userReport);
        
        ItemStack bugReport = new ItemStack(Material.REDSTONE);
        ItemMeta bugMeta = bugReport.getItemMeta();
        bugMeta.setDisplayName("§e🐛 Report Bug");
        List<String> bugLore = new ArrayList<>();
        bugLore.add("§7Report a technical issue or bug");
        bugLore.add("§7that you've encountered while");
        bugLore.add("§7playing on the server.");
        bugLore.add("");
        bugLore.add("§eClick to create a bug report");
        bugMeta.setLore(bugLore);
        bugReport.setItemMeta(bugMeta);
        inv.setItem(13, bugReport);
        
        ItemStack mapBugReport = new ItemStack(Material.MAP);
        ItemMeta mapBugMeta = mapBugReport.getItemMeta();
        mapBugMeta.setDisplayName("§6🗺 Report Map Bug");
        List<String> mapBugLore = new ArrayList<>();
        mapBugLore.add("§7Report an issue with the map");
        mapBugLore.add("§7or world, such as broken blocks,");
        mapBugLore.add("§7missing structures, or terrain issues.");
        mapBugLore.add("");
        mapBugLore.add("§7Your current location will be saved");
        mapBugLore.add("§7to help admins find the issue.");
        mapBugLore.add("");
        mapBugLore.add("§eClick to create a map bug report");
        mapBugMeta.setLore(mapBugLore);
        mapBugReport.setItemMeta(mapBugMeta);
        inv.setItem(15, mapBugReport);
        
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§b📋 Report Information");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Choose the type of report you");
        infoLore.add("§7want to create from the options above.");
        infoLore.add("");
        infoLore.add("§7All reports are reviewed by");
        infoLore.add("§7server administrators and you");
        infoLore.add("§7will receive feedback when possible.");
        infoLore.add("");
        infoLore.add("§c⚠ False reports may result in punishment!");
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(22, info);
        
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        
        player.openInventory(inv);
    }
    
    public void openUserReportMenu(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(player);
        
        if (onlinePlayers.isEmpty()) {
            player.sendMessage("§c[Reports] No other players are online to report!");
            return;
        }
        
        int size = Math.min(54, ((onlinePlayers.size() + 8) / 9) * 9);
        Inventory inv = Bukkit.createInventory(null, size, "§c👤 Select Player to Report");
        
        int slot = 0;
        for (Player target : onlinePlayers) {
            if (slot >= size - 9) break;
            
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName("§e" + target.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to report this player");
            lore.add("");
            lore.add("§7Player: §f" + target.getName());
            lore.add("§7Online for: §f" + getOnlineTime(target));
            meta.setLore(lore);
            
            playerHead.setItemMeta(meta);
            inv.setItem(slot, playerHead);
            slot++;
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("§7← Back to Report Menu");
        backItem.setItemMeta(backMeta);
        inv.setItem(inv.getSize() - 1, backItem);
        
        player.openInventory(inv);
    }
    
    private String getOnlineTime(Player player) {
        return "Unknown";
    }
    
    public void openReportTypeMenu(Player player, ReportType type, Player targetPlayer) {
        plugin.getLogger().info("Opening report type menu for " + player.getName() + " - Type: " + type + " - Target: " + (targetPlayer != null ? targetPlayer.getName() : "null"));
        
        Inventory inv = Bukkit.createInventory(null, 27, "§e📝 " + type.getDisplayName());
        plugin.getLogger().info("Created inventory with title: §e📝 " + type.getDisplayName());
        
        List<String> reasons = getCommonReasons(type);
        plugin.getLogger().info("Got " + reasons.size() + " reasons for type " + type);
        
        int slot = 10;
        for (int i = 0; i < Math.min(reasons.size(), 7); i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + reasons.get(i));
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to use this as your report title");
            lore.add("");
            if (targetPlayer != null) {
                lore.add("§7Target: §f" + targetPlayer.getName());
            }
            lore.add("§7Type: §f" + type.getDisplayName());
            meta.setLore(lore);
            
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
            
            if (slot == 17) slot = 19;
        }
        
        ItemStack customItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta customMeta = customItem.getItemMeta();
        customMeta.setDisplayName("§b✏ Custom Report");
        List<String> customLore = new ArrayList<>();
        customLore.add("§7Create a custom report with");
        customLore.add("§7your own title and description");
        customLore.add("");
        customLore.add("§eClick to create custom report");
        customMeta.setLore(customLore);
        customItem.setItemMeta(customMeta);
        inv.setItem(22, customItem);
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        if (type == ReportType.USER) {
            backMeta.setDisplayName("§7← Back to Player Selection");
        } else {
            backMeta.setDisplayName("§7← Back to Report Menu");
        }
        backItem.setItemMeta(backMeta);
        inv.setItem(18, backItem);
        
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        
        player.openInventory(inv);
    }
    
    private List<String> getCommonReasons(ReportType type) {
        List<String> reasons = new ArrayList<>();
        
        switch (type) {
            case USER:
                reasons.add("Cheating/Hacking");
                reasons.add("Griefing");
                reasons.add("Inappropriate Chat");
                reasons.add("Spam");
                reasons.add("Harassment");
                reasons.add("Inappropriate Skin/Name");
                reasons.add("Teaming (if not allowed)");
                break;
            case BUG:
                reasons.add("Item Duplication");
                reasons.add("Command Not Working");
                reasons.add("Plugin Error");
                reasons.add("Performance Issue");
                reasons.add("Inventory Bug");
                reasons.add("Permission Error");
                reasons.add("Other Technical Issue");
                break;
            case MAPBUG:
                reasons.add("Missing Blocks");
                reasons.add("Broken Structure");
                reasons.add("Terrain Glitch");
                reasons.add("Inaccessible Area");
                reasons.add("Floating Objects");
                reasons.add("Wrong Block Type");
                reasons.add("Spawn Issue");
                break;
        }
        
        return reasons;
    }
}