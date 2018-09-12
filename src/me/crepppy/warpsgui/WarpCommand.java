package me.crepppy.warpsgui;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class WarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
            return true;
        }
        Player p = (Player) sender;
        int page = 0;
        if(args.length == 0) {
            page = 1;
        } else if(args[0].equalsIgnoreCase("reload")) {
            WarpsGUI.getInstance().reloadConfig();
            p.sendMessage(ChatColor.GRAY + "WarpGUI » " + ChatColor.GREEN + "Config has been successfully reloaded");
            return true;
        } else
            try {
                page = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Invalid Page!");
            }
        Essentials essentials = WarpsGUI.getInstance().getEssentials();
        if(essentials == null)
            if(!WarpsGUI.getInstance().makeEssentials()) {
                Bukkit.getServer().getLogger().severe("Error: Essentials not found");
                p.sendMessage(ChatColor.RED + "Essentials plugin not found!");
                return true;
            }
        for(String s : essentials.getWarps().getList()) {
            if(!WarpsGUI.warps.containsKey(s)) {
                WarpsGUI.warps.put(s, 0);
                WarpsGUI.uniqueVisits.put(s, new ArrayList<>());
            }
        }
        Map<String, Integer> sortedWarps = sortByComparator(WarpsGUI.warps);
        List<String> staffWarps = WarpsGUI.getInstance().getConfig().getStringList("StaffWarps");
        ArrayList<String> toRemove = new ArrayList<>();
        for(String key : sortedWarps.keySet())
            if(staffWarps.contains(key))
                toRemove.add(key);
        for(String s : toRemove)
            sortedWarps.remove(s);
        int size = sortedWarps.size() + (staffWarps.size() >= 9 ? staffWarps.size() : 9) + 18;
        size -= ((size + 9) % 9) - 9;
        int maxpage = (int) Math.ceil((double) size / 45.0);
        if(page > maxpage)
            page = maxpage;
        if(size > 45) {
            size = 54;
        }
        Inventory i = Bukkit.createInventory(null, size, "Warps - " + page);
        int zero = 0;
        for(int x = staffWarps.size() - 1; x >= (page == 1 ? 0 : (page - 1) * 45 - 1); x--) {
            ItemStack item = new ItemStack(Material.ORANGE_CONCRETE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + staffWarps.get(x));
            item.setItemMeta(meta);
            int finalX = x;
            meta.setLore(new ArrayList<String>() {{
                add(ChatColor.DARK_GRAY + "Visited " + WarpsGUI.warps.get(staffWarps.get(finalX)) + " times.");
            }});
            i.setItem(zero, item);
            zero++;
            if(zero == size - 9)
                break;
        }
        zero = (staffWarps.size() >= 18 ? staffWarps.size() : 18);
        for(int x = sortedWarps.size() - 1; x >= (page == 1 ? 0 : (page - 1) * 45 - 1); x--) {
            ItemStack item;
            ChatColor color;
            switch(zero) {
                case 18:
                    item = new ItemStack(Material.DIAMOND_BLOCK);
                    color = ChatColor.AQUA;
                    break;
                case 19:
                    item = new ItemStack(Material.GOLD_BLOCK);
                    color = ChatColor.GOLD;
                    break;
                case 20:
                    item = new ItemStack(Material.IRON_BLOCK);
                    color = ChatColor.WHITE;
                    break;
                default:
                    item = new ItemStack(Material.BLACK_CONCRETE);
                    color = ChatColor.GRAY;
                    break;
            }
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(color + (String) sortedWarps.keySet().toArray()[x]);
            int finalX = x;
            meta.setLore(new ArrayList<String>() {{
                add(ChatColor.DARK_GRAY + "Visited " + sortedWarps.values().toArray()[finalX] + " times.");
            }});
            item.setItemMeta(meta);
            i.setItem(zero, item);
            zero++;
            if(zero == size - 9)
                break;
        }
        ItemStack back = new ItemStack(Material.ARROW);
        ItemStack forward = new ItemStack(Material.ARROW);
        ItemStack myWarps = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta backMeta = back.getItemMeta();
        ItemMeta forwardMeta = forward.getItemMeta();
        ItemMeta myWarpsMeta = myWarps.getItemMeta();
        backMeta.setDisplayName(ChatColor.GRAY + "Back");
        forwardMeta.setDisplayName(ChatColor.GRAY + "Forward");
        myWarpsMeta.setDisplayName(ChatColor.GRAY + "My Warps");
        forward.setItemMeta(forwardMeta);
        myWarps.setItemMeta(myWarpsMeta);
        back.setItemMeta(backMeta);
        if(page != 1)
            i.setItem(size - 6, back);
        if(page != maxpage)
            i.setItem(size - 4, forward);
        i.setItem(size - 5, myWarps);
        
        p.closeInventory();
        p.openInventory(i);
        return true;
    }
    
    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort(new Sort());
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for(Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    private class Sort implements Comparator<Map.Entry<String, Integer>> {
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }
}