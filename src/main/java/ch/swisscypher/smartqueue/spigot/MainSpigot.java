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

package ch.swisscypher.smartqueue.spigot;

import ch.swisscypher.smartqueue.common.constant.Channel;
import ch.swisscypher.smartqueue.spigot.api.SmartQueue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class MainSpigot extends JavaPlugin {

    private static MainSpigot instance;

    public MainSpigot() {
        super();
        instance = this;
    }

    protected MainSpigot(final JavaPluginLoader loader, final PluginDescriptionFile description, final File dataFolder, final File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }


    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channel.METHODS);
        getServer().getMessenger().registerIncomingPluginChannel(this, Channel.METHODS, MessageManager.getInstance());
        getServer().getServicesManager().register(SmartQueue.class, new SQ(), this, ServicePriority.High);
    }

    public static MainSpigot getInstance() {
        return instance;
    }
}
