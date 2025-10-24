package org.prison.casino.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Player {
    private int id;
    private UUID uuid;
    private String username;

    public Player(int id, UUID uuid, String username) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
    }

    public Player(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    // Getters
    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sauvegarde le joueur dans la base de données
     * @param connection Connexion à la base de données
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean save(Connection connection) {
        String sql = "INSERT INTO Player (uuid, username) VALUES (?, ?) ON DUPLICATE KEY UPDATE username = VALUES(username)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, username);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID généré
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
     * Charge un joueur depuis la base de données par son UUID
     * @param connection Connexion à la base de données
     * @param uuid UUID du joueur à charger
     * @return Player object ou null si non trouvé
     */
    public static Player loadByUuid(Connection connection, UUID uuid) {
        String sql = "SELECT * FROM Player WHERE uuid = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Player(
                        resultSet.getInt("id"),
                        UUID.fromString(resultSet.getString("uuid")),
                        resultSet.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Charge un joueur depuis la base de données par son ID
     * @param connection Connexion à la base de données
     * @param id ID du joueur à charger
     * @return Player object ou null si non trouvé
     */
    public static Player loadById(Connection connection, int id) {
        String sql = "SELECT * FROM Player WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Player(
                        resultSet.getInt("id"),
                        UUID.fromString(resultSet.getString("uuid")),
                        resultSet.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Supprime le joueur de la base de données
     * @param connection Connexion à la base de données
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(Connection connection) {
        String sql = "DELETE FROM Player WHERE id = ?";
        
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
        return "Player{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", username='" + username + '\'' +
                '}';
    }
}
