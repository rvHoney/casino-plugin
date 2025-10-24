package org.prison.casino.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Wallet {
    private int id;
    private int playerId;
    private int coin;
    private int token;
    private int casinoTicket;

    public Wallet(int id, int playerId, int coin, int token, int casinoTicket) {
        this.id = id;
        this.playerId = playerId;
        this.coin = coin;
        this.token = token;
        this.casinoTicket = casinoTicket;
    }

    public Wallet(int playerId, int coin, int token, int casinoTicket) {
        this.playerId = playerId;
        this.coin = coin;
        this.token = token;
        this.casinoTicket = casinoTicket;
    }

    public Wallet(int playerId) {
        this.playerId = playerId;
        this.coin = 0;
        this.token = 0;
        this.casinoTicket = 0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCoin() {
        return coin;
    }

    public int getToken() {
        return token;
    }

    public int getCasinoTicket() {
        return casinoTicket;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setCasinoTicket(int casinoTicket) {
        this.casinoTicket = casinoTicket;
    }

    // Méthodes utilitaires pour les transactions
    public boolean addCoin(int amount) {
        if (amount < 0) return false;
        this.coin += amount;
        return true;
    }

    public boolean removeCoin(int amount) {
        if (amount < 0 || this.coin < amount) return false;
        this.coin -= amount;
        return true;
    }

    public boolean addToken(int amount) {
        if (amount < 0) return false;
        this.token += amount;
        return true;
    }

    public boolean removeToken(int amount) {
        if (amount < 0 || this.token < amount) return false;
        this.token -= amount;
        return true;
    }

    public boolean addCasinoTicket(int amount) {
        if (amount < 0) return false;
        this.casinoTicket += amount;
        return true;
    }

    public boolean removeCasinoTicket(int amount) {
        if (amount < 0 || this.casinoTicket < amount) return false;
        this.casinoTicket -= amount;
        return true;
    }

    /**
     * Sauvegarde le portefeuille dans la base de données
     * @param connection Connexion à la base de données
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean save(Connection connection) {
        String sql = "INSERT INTO Wallet (playerId, coin, token, casino_ticket) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE coin = VALUES(coin), token = VALUES(token), casino_ticket = VALUES(casino_ticket)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, playerId);
            statement.setInt(2, coin);
            statement.setInt(3, token);
            statement.setInt(4, casinoTicket);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID généré si c'est un nouvel enregistrement
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Met à jour le portefeuille dans la base de données
     * @param connection Connexion à la base de données
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(Connection connection) {
        String sql = "UPDATE Wallet SET coin = ?, token = ?, casino_ticket = ? WHERE playerId = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, coin);
            statement.setInt(2, token);
            statement.setInt(3, casinoTicket);
            statement.setInt(4, playerId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Charge un portefeuille depuis la base de données par l'ID du joueur
     * @param connection Connexion à la base de données
     * @param playerId ID du joueur
     * @return Wallet object ou null si non trouvé
     */
    public static Wallet loadByPlayerId(Connection connection, int playerId) {
        String sql = "SELECT * FROM Wallet WHERE playerId = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Wallet(
                        resultSet.getInt("id"),
                        resultSet.getInt("playerId"),
                        resultSet.getInt("coin"),
                        resultSet.getInt("token"),
                        resultSet.getInt("casino_ticket")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Charge un portefeuille depuis la base de données par son ID
     * @param connection Connexion à la base de données
     * @param id ID du portefeuille
     * @return Wallet object ou null si non trouvé
     */
    public static Wallet loadById(Connection connection, int id) {
        String sql = "SELECT * FROM Wallet WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Wallet(
                        resultSet.getInt("id"),
                        resultSet.getInt("playerId"),
                        resultSet.getInt("coin"),
                        resultSet.getInt("token"),
                        resultSet.getInt("casino_ticket")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Supprime le portefeuille de la base de données
     * @param connection Connexion à la base de données
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(Connection connection) {
        String sql = "DELETE FROM Wallet WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", coin=" + coin +
                ", token=" + token +
                ", casinoTicket=" + casinoTicket +
                '}';
    }
}
