package org.prison.casino.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.prison.casino.Casino;
import org.prison.casino.managers.WalletManager;

import java.util.Arrays;
import java.util.List;

public class CasinoGUI {
    
    private final Casino plugin;
    private final WalletManager walletManager;
    private final String title;
    private final int size = 54; // 6 lignes de 9 cases
    
    public CasinoGUI(Casino plugin) {
        this.plugin = plugin;
        this.walletManager = plugin.getWalletManager();
        this.title = "Casino";
    }
    
    /**
     * Ouvre l'interface de la machine à sous pour un joueur
     * @param player Le joueur pour qui ouvrir l'interface
     */
    public void openCasinoGUI(Player player) {
        Inventory casinoInventory = Bukkit.createInventory(null, size, title);
        
        // Remplir l'inventaire avec des éléments décoratifs
        fillInventory(casinoInventory, player);
        
        // Ouvrir l'inventaire pour le joueur
        player.openInventory(casinoInventory);
        
        // Démarrer la tâche de mise à jour automatique
        startUpdateTask(player);
        
        // Logger l'ouverture
        plugin.getLogger().info("Interface casino ouverte pour: " + player.getName());
    }
    
    /**
     * Remplit l'inventaire avec des éléments décoratifs et fonctionnels
     * @param inventory L'inventaire à remplir
     * @param player Le joueur
     */
    private void fillInventory(Inventory inventory, Player player) {
        // Obtenir les informations du portefeuille du joueur
        int coins = getPlayerCoins(player);
        int tokens = getPlayerTokens(player);
        int tickets = getPlayerCasinoTickets(player);
        
        // Bordure supérieure et inférieure avec des vitres colorées
        fillBorder(inventory);

        // Boutons d'informations
        fillInfosButtons(inventory, player);
    }
    
    /**
     * Remplit les bordures de l'inventaire
     * @param inventory L'inventaire
     */
    private void fillBorder(Inventory inventory) {
        ItemStack border1 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = border1.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&8L&4E&8T&4'&8S &4G&8O &4G&8A&4M&8B&4L&8I&4N&8G"));
        border1.setItemMeta(meta);

        ItemStack border2 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta2 = border2.getItemMeta();
        meta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&8L&4E&8T&4'&8S &4G&8O &4G&8A&4M&8B&4L&8I&4N&8G"));
        border2.setItemMeta(meta2);

        inventory.setItem(0, border1);
        inventory.setItem(1, border2);
        inventory.setItem(2, border1);
        inventory.setItem(3, border2);
        inventory.setItem(4, border1);
        inventory.setItem(5, border2);
        inventory.setItem(6, border1);
        inventory.setItem(7, border2);
        inventory.setItem(8, border1);

        inventory.setItem(10, border1);
        inventory.setItem(19, border2);
        inventory.setItem(28, border1);
        inventory.setItem(37, border2);

        inventory.setItem(16, border1);
        inventory.setItem(25, border2);
        inventory.setItem(34, border1);
        inventory.setItem(43, border2);

        inventory.setItem(45, border2);
        inventory.setItem(46, border1);
        inventory.setItem(47, border2);
        inventory.setItem(48, border1);
        inventory.setItem(49, border2);
        inventory.setItem(50, border1);
        inventory.setItem(51, border2);
        inventory.setItem(52, border1);
        inventory.setItem(53, border2);
        
    }
    
    /**
     * Obtient le nombre de coins du joueur
     * @param player Le joueur
     * @return Nombre de coins
     */
    private int getPlayerCoins(Player player) {
        var wallet = walletManager.getWallet(player.getUniqueId());
        return wallet != null ? wallet.getCoin() : 0;
    }
    
    /**
     * Obtient le nombre de tokens du joueur
     * @param player Le joueur
     * @return Nombre de tokens
     */
    private int getPlayerTokens(Player player) {
        var wallet = walletManager.getWallet(player.getUniqueId());
        return wallet != null ? wallet.getToken() : 0;
    }
    
    /**
     * Obtient le nombre de tickets casino du joueur
     * @param player Le joueur
     * @return Nombre de tickets
     */
    private int getPlayerCasinoTickets(Player player) {
        var wallet = walletManager.getWallet(player.getUniqueId());
        return wallet != null ? wallet.getCasinoTicket() : 0;
    }
    
    /**
     * Remplit les boutons d'informations
     * @param inventory L'inventaire
     * @param player Le joueur
     */
    private void fillInfosButtons(Inventory inventory, Player player) {
        
        ItemStack stats = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta statsMeta = (SkullMeta) stats.getItemMeta();
        statsMeta.setOwningPlayer(player); // Définit le propriétaire de la tête
        statsMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&bSTATISTIQUES"));
        statsMeta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', "&l&eɢʟᴏʙᴀʟᴇѕ"),
            ChatColor.translateAlternateColorCodes('&', "&7- &fParties jouées: &a0"),
            ChatColor.translateAlternateColorCodes('&', "&7- &fClassement: &a0"),
            "",
            ChatColor.translateAlternateColorCodes('&', "&l&eɢᴀɪɴѕ"),
            ChatColor.translateAlternateColorCodes('&', "&7- &fCoins: &a0"),
            ChatColor.translateAlternateColorCodes('&', "&7- &fTokens: &a0"),
            ChatColor.translateAlternateColorCodes('&', "&7- &fTickets Casino: &a0")
        ));
        stats.setItemMeta(statsMeta);
        inventory.setItem(9, stats);

        ItemStack bank = new ItemStack(Material.ENDER_CHEST);
        ItemMeta bankMeta = bank.getItemMeta();
        bankMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&bBANQUE"));
        bankMeta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', "&7- &fCoins: &a" + getPlayerCoins(player)),
            ChatColor.translateAlternateColorCodes('&', "&7- &fTokens: &a" + getPlayerTokens(player)),
            ChatColor.translateAlternateColorCodes('&', "&7- &fTickets Casino: &a" + getPlayerCasinoTickets(player))
        ));
        bank.setItemMeta(bankMeta);
        inventory.setItem(18, bank);

        ItemStack leaderboard = new ItemStack(Material.OAK_SIGN);
        ItemMeta leaderboardMeta = leaderboard.getItemMeta();
        leaderboardMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&bCLASSEMENT"));
        leaderboardMeta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', "&7#&e1 &7 - &a Pseudo"),
            ChatColor.translateAlternateColorCodes('&', "&7#&e2 &7 - &a Pseudo"),
            ChatColor.translateAlternateColorCodes('&', "&7#&e3 &7 - &a Pseudo"),
            ChatColor.translateAlternateColorCodes('&', "&7#&e4 &7 - &a Pseudo"),
            ChatColor.translateAlternateColorCodes('&', "&7#&e5 &7 - &a Pseudo"),
            ChatColor.translateAlternateColorCodes('&', "&7#&e22.231 &7 - &a Pseudo &7(&eVOUS&7)")
        ));
        leaderboard.setItemMeta(leaderboardMeta);
        inventory.setItem(27, leaderboard);

        ItemStack howToPlay = new ItemStack(Material.BOOK);
        ItemMeta howToPlayMeta = howToPlay.getItemMeta();
        howToPlayMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&bCOMMENT JOUER"));
        howToPlayMeta.setLore(Arrays.asList(
            ChatColor.translateAlternateColorCodes('&', "&fUtilisez vos &a&lTickets Casino &fpour lancer la machine."),
            "",
            ChatColor.translateAlternateColorCodes('&', "&e&lᴄᴏᴍᴍᴇɴᴛ ɢᴀɢɴᴇʀ"),
            ChatColor.translateAlternateColorCodes('&', "&fVous pouvez gagner des &e&lCoins, &e&bTokens &fou,"),
            ChatColor.translateAlternateColorCodes('&', "&e&aTickets Casino &f en obtenant &c&l3 symboles identiques"),
            ChatColor.translateAlternateColorCodes('&', "&f(horizontalement, &fverticalement ou en diagonale)."),
            "",
            ChatColor.translateAlternateColorCodes('&', "&fChaque symbole identique supplémentaire"),
            ChatColor.translateAlternateColorCodes('&', "&e&lajoute 1 au multiplicateur de gain."),
            "",
            ChatColor.translateAlternateColorCodes('&', "&fAu dessus de &c&l4 symboles identiques,"),
            ChatColor.translateAlternateColorCodes('&', "&fchaque symbole octroie un &a&lTickets Casino&f."),
            "",
            ChatColor.translateAlternateColorCodes('&', "&e&lѕʏᴍʙᴏʟᴇѕ &7(&fprobabilités et gains&7)"),
            ChatColor.translateAlternateColorCodes('&', "&7- &8Charbon &7(&c0%&7) : &e100 Coins"),
            ChatColor.translateAlternateColorCodes('&', "&7- &7Fer &7(&c10%&7) : &e200 Coins"),
            ChatColor.translateAlternateColorCodes('&', "&7- &6Or &7(&c20%&7) : &e500 Coins"),
            ChatColor.translateAlternateColorCodes('&', "&7- &bDiamant &7(&c30%&7) : &e1000 Coins"),
            ChatColor.translateAlternateColorCodes('&', "&7- &aEmeraude &7(&c40%&7) : &a1 Tickets Casino"),
            ChatColor.translateAlternateColorCodes('&', "&7- &dBalise &7(&c50%&7) : &b1000 Tokens")
        ));
        howToPlay.setItemMeta(howToPlayMeta);
        inventory.setItem(36, howToPlay);
    }
    
    /**
     * Démarre la tâche de mise à jour automatique de l'interface
     * @param player Le joueur dont l'interface doit être mise à jour
     */
    private void startUpdateTask(Player player) {
        plugin.getLogger().info("Démarrage de la tâche de mise à jour pour: " + player.getName());
        
        new BukkitRunnable() {
            @Override
            public void run() {
                // Vérifier si le joueur est toujours en ligne et a l'inventaire casino ouvert
                if (!player.isOnline()) {
                    plugin.getLogger().info("Joueur déconnecté, arrêt de la tâche: " + player.getName());
                    this.cancel();
                    return;
                }
                
                if (!isCasinoInventoryOpen(player)) {
                    plugin.getLogger().info("Inventaire casino fermé, arrêt de la tâche: " + player.getName());
                    this.cancel();
                    return;
                }
                
                // Mettre à jour l'interface
                plugin.getLogger().info("Mise à jour de l'interface pour: " + player.getName());
                updateCasinoInterface(player);
            }
        }.runTaskTimer(plugin, 20L, 20L); // Démarrer après 1 seconde, puis répéter toutes les secondes
    }
    
    /**
     * Vérifie si le joueur a l'inventaire casino ouvert
     * @param player Le joueur à vérifier
     * @return true si l'inventaire casino est ouvert
     */
    private boolean isCasinoInventoryOpen(Player player) {
        if (player.getOpenInventory() == null) {
            return false;
        }
        
        String title = player.getOpenInventory().getTitle();
        return title.contains("Casino");
    }
    
    /**
     * Met à jour l'interface casino du joueur
     * @param player Le joueur dont l'interface doit être mise à jour
     */
    private void updateCasinoInterface(Player player) {
        if (player.getOpenInventory() == null) {
            plugin.getLogger().warning("Inventaire ouvert null pour: " + player.getName());
            return;
        }
        
        Inventory inventory = player.getOpenInventory().getTopInventory();
        plugin.getLogger().info("Mise à jour de la banque pour: " + player.getName() + " - Coins: " + getPlayerCoins(player));
        
        // Mettre à jour seulement les éléments qui changent (banque)
        updateBankInfo(inventory, player);
    }
    
    /**
     * Met à jour les informations de la banque dans l'inventaire
     * @param inventory L'inventaire à mettre à jour
     * @param player Le joueur
     */
    private void updateBankInfo(Inventory inventory, Player player) {
        ItemStack bank = inventory.getItem(18);
        if (bank != null && bank.getType() == Material.ENDER_CHEST) {
            ItemMeta bankMeta = bank.getItemMeta();
            bankMeta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&7- &fCoins: &a" + getPlayerCoins(player)),
                ChatColor.translateAlternateColorCodes('&', "&7- &fTokens: &a" + getPlayerTokens(player)),
                ChatColor.translateAlternateColorCodes('&', "&7- &fTickets Casino: &a" + getPlayerCasinoTickets(player))
            ));
            bank.setItemMeta(bankMeta);
        }
    }
}
