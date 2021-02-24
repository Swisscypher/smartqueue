package ch.swisscypher.smartqueue.bungee.command;

import ch.swisscypher.smartqueue.common.util.LicenseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class LicenseCommand extends Command {

    public LicenseCommand() {
        super("smartqueue");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 1) {
            return;
        }

        switch (args[0]) {
            case "c":
                Arrays.stream(LicenseManager.REDISTRIBUTE_CONDITIONS.split("\n")).forEachOrdered(
                        m -> sender.sendMessage(new TextComponent(m))
                );
                break;
            case "w":
                Arrays.stream(LicenseManager.WARRANTY.split("\n")).forEachOrdered(
                        m -> sender.sendMessage(new TextComponent(m))
                );
                break;
        }
    }
}
