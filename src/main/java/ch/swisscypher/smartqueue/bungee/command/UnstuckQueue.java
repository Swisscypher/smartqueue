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

import ch.swisscypher.smartqueue.bungee.config.Config;
import ch.swisscypher.smartqueue.bungee.exception.QueueNotExistsException;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class UnstuckQueue extends Command {

    public UnstuckQueue() {
        super("unstuck");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new Thread(() -> {
            if(args.length != 1) {
                sender.sendMessage(new TextComponent(Config.getInstance().getLabel("unstuck-usage")));
                return;
            }
            if(!sender.hasPermission(String.format("smartqueue.unstuck.%s", args[0]))) {
                sender.sendMessage(new TextComponent(Config.getInstance().getLabel("not-allowed")));
                return;
            }
            try {
                SmartQueueManager.getInstance().unstuck(args[0]);
                sender.sendMessage(new TextComponent(Config.getInstance().getLabel("unstucked", args[0])));
            } catch (QueueNotExistsException e) {
                sender.sendMessage(new TextComponent(Config.getInstance().getLabel("queue-non-existent", args[0])));
            }
        }).start();
    }
}
