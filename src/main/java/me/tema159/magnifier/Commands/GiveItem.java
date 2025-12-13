package me.tema159.magnifier.Commands;

import me.tema159.magnifier.Magnifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveItem implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;

        ((Player) commandSender).getInventory().addItem(Magnifier.getItem());
        return true;
    }
}
