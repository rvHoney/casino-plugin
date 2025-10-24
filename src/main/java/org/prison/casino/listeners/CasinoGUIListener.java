package org.prison.casino.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.prison.casino.Casino;

public class CasinoGUIListener implements Listener {
    
    private final Casino plugin;
    
    public CasinoGUIListener(Casino plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifier si c'est un joueur
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        // Vérifier si c'est l'inventaire casino
        if (isCasinoInventory(event)) {
            // Empêcher tous les clics dans l'inventaire casino
            event.setCancelled(true);
            
            // Gérer les clics sur les boutons de jeu
            handleGameButtonClick(event, player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        // Vérifier si c'est un joueur
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        // Empêcher le drag dans l'inventaire casino
        if (isCasinoInventory(event)) {
            event.setCancelled(true);
        }
    }
    
    /**
     * Vérifie si l'inventaire est l'inventaire casino
     * @param event L'événement d'inventaire
     * @return true si c'est l'inventaire casino
     */
    private boolean isCasinoInventory(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return false;
        }
        
        String title = event.getView().getTitle();
        return title.contains("Casino");
    }
    
    /**
     * Vérifie si l'inventaire est l'inventaire casino
     * @param event L'événement de drag
     * @return true si c'est l'inventaire casino
     */
    private boolean isCasinoInventory(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return false;
        }
        
        String title = event.getView().getTitle();
        return title.contains("Casino");
    }
    
    /**
     * Gère les clics sur les boutons de jeu
     * @param event L'événement de clic
     * @param player Le joueur qui a cliqué
     */
    private void handleGameButtonClick(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        String itemName = clickedItem.getItemMeta().getDisplayName();
        
        // Vérifier les différents boutons de jeu
        if (itemName.contains("Machine à Sous Classique")) {
            handleSlotMachineClick(player);
        } else if (itemName.contains("Roulette")) {
            handleRouletteClick(player);
        } else if (itemName.contains("Blackjack")) {
            handleBlackjackClick(player);
        }
    }
    
    /**
     * Gère le clic sur le bouton Machine à Sous
     * @param player Le joueur
     */
    private void handleSlotMachineClick(Player player) {
        player.sendMessage("Machine a Sous Classique");
        player.sendMessage("Cette fonctionnalite sera bientot disponible!");
        player.sendMessage("Cout: 10 tokens");
        
        // TODO: Implémenter la logique de la machine à sous
    }
    
    /**
     * Gère le clic sur le bouton Roulette
     * @param player Le joueur
     */
    private void handleRouletteClick(Player player) {
        player.sendMessage("Roulette");
        player.sendMessage("Cette fonctionnalite sera bientot disponible!");
        player.sendMessage("Cout: 25 tokens");
        
        // TODO: Implémenter la logique de la roulette
    }
    
    /**
     * Gère le clic sur le bouton Blackjack
     * @param player Le joueur
     */
    private void handleBlackjackClick(Player player) {
        player.sendMessage("Blackjack");
        player.sendMessage("Cette fonctionnalite sera bientot disponible!");
        player.sendMessage("Cout: 50 tokens");
        
        // TODO: Implémenter la logique du blackjack
    }
}
