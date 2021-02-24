package ch.swisscypher.smartqueue.spigot.command;

import ch.swisscypher.smartqueue.common.util.LicenseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class LicenseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            return false;
        }

        switch (args[0]) {
            case "c":
                Arrays.stream(LicenseManager.REDISTRIBUTE_CONDITIONS.split("\n")).forEachOrdered(sender::sendMessage);
                return true;
            case "w":
                Arrays.stream(LicenseManager.WARRANTY.split("\n")).forEachOrdered(sender::sendMessage);
                return true;
        }

        return false;
    }
}
