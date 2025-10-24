package org.prison.casino;

import org.bukkit.plugin.java.JavaPlugin;
import org.prison.casino.commands.AdminPayCommand;
import org.prison.casino.commands.CasinoCommand;
import org.prison.casino.database.DatabaseManager;
import org.prison.casino.listeners.CasinoGUIListener;
import org.prison.casino.managers.PlayerManager;
import org.prison.casino.managers.WalletManager;

public final class Casino extends JavaPlugin {
    
    private DatabaseManager databaseManager;
    private PlayerManager playerManager;
    private WalletManager walletManager;

    @Override
    public void onEnable() {
        // Initialiser la base de données
        initializeDatabase();
        
        // Initialiser les gestionnaires
        initializeManagers();
        
        // Enregistrer les commandes
        registerCommands();
        
        // Enregistrer les listeners
        registerListeners();
        
        getLogger().info("Plugin Casino activé avec succès!");
    }

    @Override
    public void onDisable() {
        // Fermer la connexion à la base de données
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        
        getLogger().info("Plugin Casino désactivé!");
    }
    
    /**
     * Initialise la connexion à la base de données et crée les tables
     */
    private void initializeDatabase() {
        try {
            databaseManager = new DatabaseManager(this);
            databaseManager.connect();
            
            if (databaseManager.isConnected()) {
                getLogger().info("Base de données initialisée avec succès!");
            } else {
                getLogger().severe("Échec de l'initialisation de la base de données!");
            }
        } catch (Exception e) {
            getLogger().severe("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialise les gestionnaires
     */
    private void initializeManagers() {
        if (databaseManager != null && databaseManager.isConnected()) {
            playerManager = new PlayerManager(databaseManager, getLogger());
            walletManager = new WalletManager(databaseManager, getLogger());
            getLogger().info("Gestionnaires initialisés avec succès!");
        } else {
            getLogger().severe("Impossible d'initialiser les gestionnaires - base de données non connectée!");
        }
    }
    
    /**
     * Retourne le gestionnaire de base de données
     * @return DatabaseManager instance
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    /**
     * Retourne le gestionnaire de joueurs
     * @return PlayerManager instance
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    /**
     * Retourne le gestionnaire de portefeuilles
     * @return WalletManager instance
     */
    public WalletManager getWalletManager() {
        return walletManager;
    }
    
    /**
     * Enregistre toutes les commandes du plugin
     */
    private void registerCommands() {
        // Enregistrer la commande adminpay
        AdminPayCommand adminPayCommand = new AdminPayCommand(this);
        getCommand("adminpay").setExecutor(adminPayCommand);
        getCommand("adminpay").setTabCompleter(adminPayCommand);
        
        // Enregistrer la commande casino
        CasinoCommand casinoCommand = new CasinoCommand(this);
        getCommand("casino").setExecutor(casinoCommand);
        getCommand("casino").setTabCompleter(casinoCommand);
        
        getLogger().info("Commandes enregistrées avec succès!");
    }
    
    /**
     * Enregistre tous les listeners du plugin
     */
    private void registerListeners() {
        // Enregistrer le listener pour l'interface casino
        getServer().getPluginManager().registerEvents(new CasinoGUIListener(this), this);
        
        getLogger().info("Listeners enregistrés avec succès!");
    }
}
