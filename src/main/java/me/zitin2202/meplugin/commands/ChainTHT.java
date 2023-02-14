package me.zitin2202.meplugin.commands;

import me.zitin2202.meplugin.MePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

public class ChainTHT implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player){
            Player player = (Player)commandSender;
            TNTPrimed tnt = player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
            String chain_THT_key = "chain_THT_key";
            tnt.setMetadata(chain_THT_key,new FixedMetadataValue(MePlugin.getPlugin(),0));
        }

        return false;
    }
}