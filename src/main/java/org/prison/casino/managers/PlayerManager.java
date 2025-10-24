package org.prison.casino.managers;

import org.prison.casino.database.DatabaseManager;
import org.prison.casino.models.Player;
import org.prison.casino.models.Wallet;

import java.sql.Connection;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerManager {
    private final DatabaseManager databaseManager;
    private final Logger logger;

    public PlayerManager(DatabaseManager databaseManager, Logger logger) {
        this.databaseManager = databaseManager;
        this.logger = logger;
    }

    /**
     * Crée un nouveau joueur avec son portefeuille
     * @param uuid UUID du joueur
     * @param username Nom d'utilisateur du joueur
     * @return Player object créé ou null en cas d'erreur
     */
    public Player createPlayer(UUID uuid, String username) {
        if (!databaseManager.isConnected()) {
            logger.severe("Base de données non connectée!");
            return null;
        }

        try {
            Connection connection = databaseManager.getConnection();
            
            // Créer le joueur
            Player player = new Player(uuid, username);
            if (player.save(connection)) {
                logger.info("Joueur créé avec succès: " + username);
                
                // Créer le portefeuille pour ce joueur
                Wallet wallet = new Wallet(player.getId());
                if (wallet.save(connection)) {
                    logger.info("Portefeuille créé avec succès pour le joueur: " + username);
                } else {
                    logger.warning("Erreur lors de la création du portefeuille pour: " + username);
                }
                
                return player;
            } else {
                logger.severe("Erreur lors de la création du joueur: " + username);
                return null;
            }
        } catch (Exception e) {
            logger.severe("Erreur lors de la création du joueur: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère un joueur par son UUID
     * @param uuid UUID du joueur
     * @return Player object ou null si non trouvé
     */
    public Player getPlayer(UUID uuid) {
        if (!databaseManager.isConnected()) {
            logger.severe("Base de données non connectée!");
            return null;
        }

        try {
            Connection connection = databaseManager.getConnection();
            Player player = Player.loadByUuid(connection, uuid);
            
            if (player != null) {
                logger.info("Joueur trouvé: " + player.getUsername());
            } else {
                logger.info("Joueur non trouvé pour l'UUID: " + uuid);
            }
            
            return player;
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération du joueur: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère un joueur par son nom d'utilisateur
     * @param username Nom d'utilisateur du joueur
     * @return Player object ou null si non trouvé
     */
    public Player getPlayerByUsername(String username) {
        if (!databaseManager.isConnected()) {
            logger.severe("Base de données non connectée!");
            return null;
        }

        try {
            Connection connection = databaseManager.getConnection();
            String sql = "SELECT * FROM Player WHERE username = ?";
            
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Player(
                            resultSet.getInt("id"),
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("username")
                        );
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération du joueur par nom: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Supprime un joueur et son portefeuille
     * @param uuid UUID du joueur à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deletePlayer(UUID uuid) {
        if (!databaseManager.isConnected()) {
            logger.severe("Base de données non connectée!");
            return false;
        }

        try {
            Connection connection = databaseManager.getConnection();
            Player player = Player.loadByUuid(connection, uuid);
            
            if (player != null) {
                boolean deleted = player.delete(connection);
                if (deleted) {
                    logger.info("Joueur supprimé avec succès: " + player.getUsername());
                } else {
                    logger.warning("Erreur lors de la suppression du joueur: " + player.getUsername());
                }
                return deleted;
            } else {
                logger.warning("Joueur non trouvé pour suppression: " + uuid);
                return false;
            }
        } catch (Exception e) {
            logger.severe("Erreur lors de la suppression du joueur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifie si un joueur existe
     * @param uuid UUID du joueur
     * @return true si le joueur existe, false sinon
     */
    public boolean playerExists(UUID uuid) {
        return getPlayer(uuid) != null;
    }

    /**
     * Met à jour le nom d'utilisateur d'un joueur
     * @param uuid UUID du joueur
     * @param newUsername Nouveau nom d'utilisateur
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updatePlayerUsername(UUID uuid, String newUsername) {
        if (!databaseManager.isConnected()) {
            logger.severe("Base de données non connectée!");
            return false;
        }

        try {
            Connection connection = databaseManager.getConnection();
            Player player = Player.loadByUuid(connection, uuid);
            
            if (player != null) {
                player.setUsername(newUsername);
                boolean updated = player.save(connection);
                
                if (updated) {
                    logger.info("Nom d'utilisateur mis à jour pour: " + uuid + " -> " + newUsername);
                } else {
                    logger.warning("Erreur lors de la mise à jour du nom d'utilisateur: " + uuid);
                }
                
                return updated;
            } else {
                logger.warning("Joueur non trouvé pour mise à jour: " + uuid);
                return false;
            }
        } catch (Exception e) {
            logger.severe("Erreur lors de la mise à jour du nom d'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
