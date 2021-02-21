/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021 Zayceur (dev@zayceur.ch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.swisscypher.smartqueue.bungee;

import ch.swisscypher.smartqueue.bungee.command.BypassQueue;
import ch.swisscypher.smartqueue.bungee.command.JoinQueue;
import ch.swisscypher.smartqueue.bungee.command.ToggleQueue;
import ch.swisscypher.smartqueue.bungee.config.Config;
import ch.swisscypher.smartqueue.bungee.event.MessageEvent;
import ch.swisscypher.smartqueue.bungee.event.PlayerEvent;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import ch.swisscypher.smartqueue.common.constant.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class MainBungee extends Plugin {

    private static MainBungee instance;

    public MainBungee() {
        super();
        instance = this;
    }

    public MainBungee(ProxyServer proxy, PluginDescription description) {
        super(proxy, description);
        instance = this;
    }

    public static MainBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Config.getInstance().load();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new JoinQueue());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ToggleQueue());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BypassQueue());
        getProxy().getPluginManager().registerListener(this, new PlayerEvent());
        getProxy().getPluginManager().registerListener(this, new MessageEvent());
        getProxy().registerChannel(Channel.METHODS);
    }

    @Override
    public void onDisable() {
        SmartQueueManager.getInstance().stop();
    }
}
