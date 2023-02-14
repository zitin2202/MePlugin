package me.zitin2202.meplugin.commands.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sound implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player){
            Player player = ((Player)commandSender);
            player.playSound(player, org.bukkit.Sound.valueOf(strings[0]),6f,6f);
            return true;
        }

        return false;
    }
}
