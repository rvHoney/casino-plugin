package org.prison.casino.managers;

import org.prison.casino.database.DatabaseManager;
import org.prison.casino.models.Player;
import org.prison.casino.models.Wallet;

import java.sql.Connection;
import java.util.UUID;
import java.util.logging.Logger;

public class WalletManager {
    private final DatabaseManager databaseManager;
    private final Logger logger;

    public WalletManager(DatabaseManager databaseManager, Logger logger) {
        this.databaseManager = databaseManager;
        this.logger = logger;
    }

    /**
     * Récupère le portefeuille d'un joueur par son UUID
     * @param playerUuid UUID du joueur
     * @return Wallet object ou null si non trouvé
     */
    public Wallet getWallet(UUID playerUuid) {
        if (!databaseManager.isConnected()) {
            logger.severe("Base de données non connectée!");
            return null;
        }

        try {
            Connection connection = databaseManager.getConnection();
            
            // D'abord récupérer le joueur
            Player player = Player.loadByUuid(connection, playerUuid);
            if (player == null) {
                logger.warning("Joueur non trouvé pour récupération du portefeuille: " + playerUuid);
                return null;
            }
            
            // Puis récupérer le portefeuille
            Wallet wallet = Wallet.loadByPlayerId(connection, player.getId());
            
            if (wallet != null) {
                logger.info("Portefeuille trouvé pour: " + player.getUsername());
            } else {
                logger.info("Portefeuille non trouvé pour: " + player.getUsername());
            }
            
            return wallet;
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération du portefeuille: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ajoute des coins au portefeuille d'un joueur
     * @param playerUuid UUID du joueur
     * @param amount Montant à ajouter
     * @return true si l'opération a réussi, false sinon
     */
    public boolean addCoins(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        if (wallet == null) {
            logger.warning("Portefeuille non trouvé pour ajout de coins: " + playerUuid);
            return false;
        }

        if (wallet.addCoin(amount)) {
            boolean updated = wallet.update(databaseManager.getConnection());
            if (updated) {
                logger.info("Coins ajoutés avec succès: " + amount + " pour " + playerUuid);
            } else {
                logger.warning("Erreur lors de la sauvegarde des coins pour: " + playerUuid);
            }
            return updated;
        }
        
        return false;
    }

    /**
     * Retire des coins du portefeuille d'un joueur
     * @param playerUuid UUID du joueur
     * @param amount Montant à retirer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean removeCoins(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        if (wallet == null) {
            logger.warning("Portefeuille non trouvé pour retrait de coins: " + playerUuid);
            return false;
        }

        if (wallet.removeCoin(amount)) {
            boolean updated = wallet.update(databaseManager.getConnection());
            if (updated) {
                logger.info("Coins retirés avec succès: " + amount + " pour " + playerUuid);
            } else {
                logger.warning("Erreur lors de la sauvegarde des coins pour: " + playerUuid);
            }
            return updated;
        } else {
            logger.warning("Fonds insuffisants pour retrait de coins: " + playerUuid);
            return false;
        }
    }

    /**
     * Ajoute des tokens au portefeuille d'un joueur
     * @param playerUuid UUID du joueur
     * @param amount Montant à ajouter
     * @return true si l'opération a réussi, false sinon
     */
    public boolean addTokens(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        if (wallet == null) {
            logger.warning("Portefeuille non trouvé pour ajout de tokens: " + playerUuid);
            return false;
        }

        if (wallet.addToken(amount)) {
            boolean updated = wallet.update(databaseManager.getConnection());
            if (updated) {
                logger.info("Tokens ajoutés avec succès: " + amount + " pour " + playerUuid);
            } else {
                logger.warning("Erreur lors de la sauvegarde des tokens pour: " + playerUuid);
            }
            return updated;
        }
        
        return false;
    }

    /**
     * Retire des tokens du portefeuille d'un joueur
     * @param playerUuid UUID du joueur
     * @param amount Montant à retirer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean removeTokens(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        if (wallet == null) {
            logger.warning("Portefeuille non trouvé pour retrait de tokens: " + playerUuid);
            return false;
        }

        if (wallet.removeToken(amount)) {
            boolean updated = wallet.update(databaseManager.getConnection());
            if (updated) {
                logger.info("Tokens retirés avec succès: " + amount + " pour " + playerUuid);
            } else {
                logger.warning("Erreur lors de la sauvegarde des tokens pour: " + playerUuid);
            }
            return updated;
        } else {
            logger.warning("Fonds insuffisants pour retrait de tokens: " + playerUuid);
            return false;
        }
    }

    /**
     * Ajoute des tickets casino au portefeuille d'un joueur
     * @param playerUuid UUID du joueur
     * @param amount Montant à ajouter
     * @return true si l'opération a réussi, false sinon
     */
    public boolean addCasinoTickets(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        if (wallet == null) {
            logger.warning("Portefeuille non trouvé pour ajout de tickets: " + playerUuid);
            return false;
        }

        if (wallet.addCasinoTicket(amount)) {
            boolean updated = wallet.update(databaseManager.getConnection());
            if (updated) {
                logger.info("Tickets casino ajoutés avec succès: " + amount + " pour " + playerUuid);
            } else {
                logger.warning("Erreur lors de la sauvegarde des tickets pour: " + playerUuid);
            }
            return updated;
        }
        
        return false;
    }

    /**
     * Retire des tickets casino du portefeuille d'un joueur
     * @param playerUuid UUID du joueur
     * @param amount Montant à retirer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean removeCasinoTickets(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        if (wallet == null) {
            logger.warning("Portefeuille non trouvé pour retrait de tickets: " + playerUuid);
            return false;
        }

        if (wallet.removeCasinoTicket(amount)) {
            boolean updated = wallet.update(databaseManager.getConnection());
            if (updated) {
                logger.info("Tickets casino retirés avec succès: " + amount + " pour " + playerUuid);
            } else {
                logger.warning("Erreur lors de la sauvegarde des tickets pour: " + playerUuid);
            }
            return updated;
        } else {
            logger.warning("Fonds insuffisants pour retrait de tickets: " + playerUuid);
            return false;
        }
    }

    /**
     * Vérifie si un joueur a suffisamment de coins
     * @param playerUuid UUID du joueur
     * @param amount Montant à vérifier
     * @return true si le joueur a suffisamment de coins, false sinon
     */
    public boolean hasEnoughCoins(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        return wallet != null && wallet.getCoin() >= amount;
    }

    /**
     * Vérifie si un joueur a suffisamment de tokens
     * @param playerUuid UUID du joueur
     * @param amount Montant à vérifier
     * @return true si le joueur a suffisamment de tokens, false sinon
     */
    public boolean hasEnoughTokens(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        return wallet != null && wallet.getToken() >= amount;
    }

    /**
     * Vérifie si un joueur a suffisamment de tickets casino
     * @param playerUuid UUID du joueur
     * @param amount Montant à vérifier
     * @return true si le joueur a suffisamment de tickets, false sinon
     */
    public boolean hasEnoughCasinoTickets(UUID playerUuid, int amount) {
        Wallet wallet = getWallet(playerUuid);
        return wallet != null && wallet.getCasinoTicket() >= amount;
    }
}
