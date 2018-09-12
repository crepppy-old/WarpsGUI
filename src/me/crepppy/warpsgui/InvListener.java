package me.crepppy.warpsgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InvListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            if(e.getCurrentItem() == null)
                return;
            if(ChatColor.stripColor(e.getInventory().getTitle()).toLowerCase().startsWith("warps - ")) {
                String s = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                e.setCancelled(true);
                if(s.equalsIgnoreCase("back") && e.getCurrentItem().getType() == Material.ARROW) {
                    ((Player) e.getWhoClicked()).performCommand("warps " + String.valueOf(Integer.parseInt(ChatColor.stripColor(e.getInventory().getTitle()).split("-")[1].trim()) - 1));
                } else if(s.equalsIgnoreCase("forward") && e.getCurrentItem().getType() == Material.ARROW) {
                    ((Player) e.getWhoClicked()).performCommand("warps " + String.valueOf(Integer.parseInt(ChatColor.stripColor(e.getInventory().getTitle()).split("-")[1].trim()) + 1));
                } else if(s.equalsIgnoreCase("My Warps") && e.getCurrentItem().getType() == Material.END_PORTAL_FRAME) {
                    //TODO OPEN INVENTORY WITH PLAYER WARPS
                    List<String> myWarps = new ArrayList<>();
                    for(Map.Entry<String, UUID> entry : WarpsGUI.warpOwner.entrySet()) {
                        if(entry.getValue() == e.getWhoClicked().getUniqueId())
                            myWarps.add(entry.getKey());
                    }
                    int size = myWarps.size() + 9;
                    size -= ((size + 9) % 9) - 9;
                    Inventory i = Bukkit.createInventory(null, size, "My Warps");
                    int zero = 0;
                    for(String myWarp : myWarps) {
                        ItemStack item = new ItemStack(Material.BLACK_CONCRETE);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ChatColor.GRAY + myWarp);
                        meta.setLore(new ArrayList<String>() {{
                            add(ChatColor.GRAY + "" + ChatColor.BOLD + "Left Click " + ChatColor.RESET + ChatColor.GRAY + "to teleport to this warp");
                            add(ChatColor.RED + "" + ChatColor.BOLD + "Right Click " + ChatColor.RESET + ChatColor.GRAY + "to delete this warp");
                        }});
                        item.setItemMeta(meta);
                        i.setItem(zero, item);
                        zero++;
                        if(zero == size - 9)
                            break;
                    }
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta meta = barrier.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "Close");
                    barrier.setItemMeta(meta);
                    i.setItem(size - 5, barrier);
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(i);
                } else {
                    ((Player) e.getWhoClicked()).performCommand("warp " + s);
                    if(WarpsGUI.uniqueVisits.containsKey(s) && WarpsGUI.uniqueVisits.get(s).contains(e.getWhoClicked().getUniqueId()))
                        return;
                    if(WarpsGUI.warps.keySet().contains(s)) {
                        WarpsGUI.warps.replace(s, WarpsGUI.warps.get(s) + 1);
                        List<UUID> newList = WarpsGUI.uniqueVisits.get(s);
                        if(newList == null) {
                            newList = new ArrayList<UUID>() {{
                                add(e.getWhoClicked().getUniqueId());
                            }};
                            WarpsGUI.uniqueVisits.put(s, newList);
                        }
                        else {
                            newList.add(e.getWhoClicked().getUniqueId());
                        }
                       
                    } else {
                        WarpsGUI.warps.put(s, 1);
                        WarpsGUI.uniqueVisits.put(s, new ArrayList<UUID>() {{
                            add(e.getWhoClicked().getUniqueId());
                        }});
                    }
                }
            }
        }
        if(ChatColor.stripColor(e.getInventory().getTitle()).equals("My Warps")) {
            String s = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            e.setCancelled(true);
            if(s.equalsIgnoreCase("close") && e.getCurrentItem().getType() == Material.BARRIER) {
                e.getWhoClicked().closeInventory();
                ((Player) e.getWhoClicked()).performCommand("warps");
            } else {
                if(e.getClick() == ClickType.RIGHT) {
                    ((Player) e.getWhoClicked()).performCommand("delwarp " + s);
                    WarpsGUI.warpOwner.remove(s);
                    WarpsGUI.uniqueVisits.remove(s);
                    WarpsGUI.playerWarps.replace((Player) e.getWhoClicked(), WarpsGUI.playerWarps.get(e.getWhoClicked()) - 1);
                    WarpsGUI.warps.remove(s);
                } else {
                    ((Player) e.getWhoClicked()).performCommand("warp " + s);
                    if(WarpsGUI.uniqueVisits.containsKey(s) && WarpsGUI.uniqueVisits.get(s).contains(e.getWhoClicked().getUniqueId()))
                        return;
                    if(WarpsGUI.warps.keySet().contains(s)) {
                        WarpsGUI.warps.replace(s, WarpsGUI.warps.get(s) + 1);
                        List<UUID> newList = WarpsGUI.uniqueVisits.get(s);
                        if(newList == null) {
                            newList = new ArrayList<UUID>() {{
                                add(e.getWhoClicked().getUniqueId());
                            }};
                            WarpsGUI.uniqueVisits.put(s, newList);
                        }
                        else {
                            newList.add(e.getWhoClicked().getUniqueId());
                            WarpsGUI.uniqueVisits.replace(s, newList);
                        }
                    } else {
                        WarpsGUI.warps.put(s, 1);
                        WarpsGUI.uniqueVisits.put(s, new ArrayList<UUID>() {{
                            add(e.getWhoClicked().getUniqueId());
                        }});
                    }
                }
            }
        }
    }
}
