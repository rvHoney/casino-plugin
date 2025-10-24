package org.prison.casino.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.prison.casino.Casino;
import org.prison.casino.gui.CasinoGUI;
import org.prison.casino.managers.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class CasinoCommand implements CommandExecutor, TabCompleter {
    
    private final Casino plugin;
    private final PlayerManager playerManager;
    private final CasinoGUI casinoGUI;
    
    public CasinoCommand(Casino plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.casinoGUI = new CasinoGUI(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier si l'expéditeur est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "❌ Cette commande ne peut être utilisée que par un joueur!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Vérifier si le joueur existe dans notre système
        if (!playerManager.playerExists(player.getUniqueId())) {
            // Créer le joueur s'il n'existe pas
            var newPlayer = playerManager.createPlayer(player.getUniqueId(), player.getName());
            if (newPlayer == null) {
                player.sendMessage(ChatColor.RED + "❌ Erreur lors de la création de votre profil casino!");
                return true;
            }
            player.sendMessage(ChatColor.GREEN + "✅ Profil casino créé avec succès!");
        }
        
        // Ouvrir l'interface casino
        casinoGUI.openCasinoGUI(player);
        
        // Message de confirmation
        player.sendMessage(ChatColor.GOLD + "🎰 Interface casino ouverte!");
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // La commande casino n'a pas d'arguments, donc pas d'autocomplétion nécessaire
        return new ArrayList<>();
    }
}
