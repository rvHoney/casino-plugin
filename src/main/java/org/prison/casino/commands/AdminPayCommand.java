package org.prison.casino.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.prison.casino.Casino;
import org.prison.casino.managers.PlayerManager;
import org.prison.casino.managers.WalletManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AdminPayCommand implements CommandExecutor, TabCompleter {
    
    private final Casino plugin;
    private final PlayerManager playerManager;
    private final WalletManager walletManager;
    
    // Types de monnaie disponibles
    private final List<String> moneyTypes = Arrays.asList("coin", "token", "casino_ticket");
    
    public AdminPayCommand(Casino plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.walletManager = plugin.getWalletManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier si l'expéditeur est OP
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "❌ Vous devez être OP pour utiliser cette commande!");
            return true;
        }
        
        // Vérifier le nombre d'arguments
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "❌ Usage: /adminpay <pseudo> <moneyType> <amount>");
            sender.sendMessage(ChatColor.YELLOW + "Types de monnaie disponibles: coin, token, casino_ticket");
            return true;
        }
        
        String targetUsername = args[0];
        String moneyType = args[1].toLowerCase();
        int amount;
        
        // Vérifier si le type de monnaie est valide
        if (!moneyTypes.contains(moneyType)) {
            sender.sendMessage(ChatColor.RED + "❌ Type de monnaie invalide!");
            sender.sendMessage(ChatColor.YELLOW + "Types disponibles: coin, token, casino_ticket");
            return true;
        }
        
        // Vérifier si le montant est un nombre valide
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "❌ Le montant doit être un nombre positif!");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "❌ Le montant doit être un nombre valide!");
            return true;
        }
        
        // Trouver le joueur cible
        Player targetPlayer = Bukkit.getPlayer(targetUsername);
        UUID targetUuid;
        
        if (targetPlayer != null) {
            targetUuid = targetPlayer.getUniqueId();
        } else {
            // Le joueur n'est pas en ligne, essayer de le trouver dans la base de données
            org.prison.casino.models.Player playerData = playerManager.getPlayerByUsername(targetUsername);
            if (playerData == null) {
                sender.sendMessage(ChatColor.RED + "❌ Joueur '" + targetUsername + "' introuvable!");
                return true;
            }
            targetUuid = playerData.getUuid();
        }
        
        // Vérifier si le joueur existe dans notre système
        org.prison.casino.models.Player player = playerManager.getPlayer(targetUuid);
        if (player == null) {
            // Créer le joueur s'il n'existe pas
            player = playerManager.createPlayer(targetUuid, targetUsername);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "❌ Erreur lors de la création du joueur!");
                return true;
            }
        }
        
        // Effectuer le paiement selon le type de monnaie
        boolean success = false;
        String moneyTypeDisplay = getMoneyTypeDisplay(moneyType);
        
        switch (moneyType) {
            case "coin":
                success = walletManager.addCoins(targetUuid, amount);
                break;
            case "token":
                success = walletManager.addTokens(targetUuid, amount);
                break;
            case "casino_ticket":
                success = walletManager.addCasinoTickets(targetUuid, amount);
                break;
        }
        
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "✅ " + amount + " " + moneyTypeDisplay + " ajoutés à " + targetUsername + "!");
            
            // Notifier le joueur s'il est en ligne
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.sendMessage(ChatColor.GREEN + "💰 Vous avez reçu " + amount + " " + moneyTypeDisplay + " de la part d'un administrateur!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "❌ Erreur lors de l'ajout de " + moneyTypeDisplay + "!");
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!sender.isOp()) {
            return completions;
        }
        
        switch (args.length) {
            case 1:
                // Complétion pour les noms de joueurs
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
                break;
                
            case 2:
                // Complétion pour les types de monnaie
                for (String moneyType : moneyTypes) {
                    if (moneyType.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(moneyType);
                    }
                }
                break;
                
            case 3:
                // Complétion pour les montants (suggestions communes)
                String[] commonAmounts = {"10", "50", "100", "500", "1000", "5000", "10000"};
                for (String amount : commonAmounts) {
                    if (amount.startsWith(args[2])) {
                        completions.add(amount);
                    }
                }
                break;
        }
        
        return completions;
    }
    
    /**
     * Retourne l'affichage formaté du type de monnaie
     * @param moneyType Type de monnaie
     * @return Affichage formaté
     */
    private String getMoneyTypeDisplay(String moneyType) {
        switch (moneyType) {
            case "coin":
                return "coins";
            case "token":
                return "tokens";
            case "casino_ticket":
                return "tickets casino";
            default:
                return moneyType;
        }
    }
}
