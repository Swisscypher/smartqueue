/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021-2022 Zayceur (dev@zayceur.ch)
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

import ch.swisscypher.smartqueue.spigot.MainSpigot;
import ch.swisscypher.smartqueue.spigot.SQ;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {
    SQ sq;

    public JoinCommand(SQ sq) {
        this.sq = sq;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MainSpigot.getInstance().getThreadPool().execute(() -> {
            if (sender instanceof Player) {
                if (args.length != 1 || !sender.hasPermission(String.format("smartqueue.join.%s", args[0]))) {
                    return;
                }
                sq.addPlayer((Player) sender, args[0]);
            }
        });
        return false;
    }
}
