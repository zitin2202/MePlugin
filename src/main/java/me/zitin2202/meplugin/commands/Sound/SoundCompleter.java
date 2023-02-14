package me.zitin2202.meplugin.commands.Sound;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoundCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 1){
            return Stream.of(Sound.values()).map(Sound::name).collect(Collectors.toList());
        }
        return null;
    }
}
