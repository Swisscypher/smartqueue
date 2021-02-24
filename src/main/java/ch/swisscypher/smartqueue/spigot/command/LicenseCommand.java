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
