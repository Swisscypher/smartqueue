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
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BypassQueue extends Command {

    public BypassQueue() {
        super("bypass");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new Thread(() -> {
            if ((sender instanceof ProxiedPlayer)) {
                if(args.length != 1) {
                    sender.sendMessage(new TextComponent(Config.getInstance().lang.getConfiguration().getString("bypass-usage")));
                    return;
                }
                if(!sender.hasPermission(String.format("smartqueue.bypass.%s", args[0]))) {
                    sender.sendMessage(new TextComponent(Config.getInstance().lang.getConfiguration().getString("not-allowed")));
                    return;
                }
                ProxiedPlayer p = (ProxiedPlayer) sender;
                try {
                    SmartQueueManager.getInstance().addPlayerToQueueWithCustomPriority(args[0], p, Integer.MAX_VALUE);
                } catch (QueueNotExistsException e) {
                    sender.sendMessage(new TextComponent(String.format(Config.getInstance().lang.getConfiguration().getString("queue-non-existent"), args[0])));
                }
            }
        }).start();
    }
}
