package org.prison.casino.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.prison.casino.Casino;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseManager {
    private final Casino plugin;
    private final Logger logger;
    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseManager(Casino plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        
        // Charger la configuration
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        this.host = config.getString("database.host", "localhost");
        this.port = config.getInt("database.port", 3306);
        this.database = config.getString("database.database", "casino_db");
        this.username = config.getString("database.username", "casino_user");
        this.password = config.getString("database.password", "casino_password");
    }

    /**
     * Établit la connexion à la base de données MySQL
     */
    public void connect() {
        try {
            // Construire l'URL de connexion MySQL simple
            String connectionUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);

            connection = DriverManager.getConnection(connectionUrl, username, password);
            logger.info("Connexion à la base de données MySQL établie avec succès");
            
            // Créer les tables si elles n'existent pas
            createTables();
            
        } catch (SQLException e) {
            logger.severe("Erreur lors de la connexion à la base de données MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée les tables Player et Wallet si elles n'existent pas
     */
    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            
            // Créer la table Player
            String createPlayerTable = """
                CREATE TABLE IF NOT EXISTS Player (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    uuid VARCHAR(36) NOT NULL UNIQUE,
                    username VARCHAR(16) NOT NULL,
                    INDEX idx_uuid (uuid),
                    INDEX idx_username (username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
            
            statement.execute(createPlayerTable);
            logger.info("Table Player créée/vérifiée avec succès");
            
            // Créer la table Wallet
            String createWalletTable = """
                CREATE TABLE IF NOT EXISTS Wallet (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    playerId INT NOT NULL,
                    coin INT DEFAULT 0,
                    token INT DEFAULT 0,
                    casino_ticket INT DEFAULT 0,
                    FOREIGN KEY (playerId) REFERENCES Player(id) ON DELETE CASCADE,
                    INDEX idx_playerId (playerId)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
            
            statement.execute(createWalletTable);
            logger.info("Table Wallet créée/vérifiée avec succès");
            
        } catch (SQLException e) {
            logger.severe("Erreur lors de la création des tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retourne la connexion à la base de données
     * @return Connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Vérifie si la connexion est active
     * @return true si la connexion est active, false sinon
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
