/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021 Zayceur (dev@zayceur.ch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.swisscypher.smartqueue.bungee.command;

import ch.swisscypher.smartqueue.common.util.LicenseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class License extends Command {

    public License() {
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
