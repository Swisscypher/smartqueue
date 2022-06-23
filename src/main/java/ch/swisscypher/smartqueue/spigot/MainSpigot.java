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

package ch.swisscypher.smartqueue.spigot;

import ch.swisscypher.smartqueue.common.constant.Channel;
import ch.swisscypher.smartqueue.common.util.LicenseManager;
import ch.swisscypher.smartqueue.spigot.api.SmartQueue;
import ch.swisscypher.smartqueue.spigot.citizens.trait.JoinQueueTrait;
import ch.swisscypher.smartqueue.spigot.command.LicenseCommand;
import ch.swisscypher.smartqueue.spigot.deluxehub.JoinAction;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainSpigot extends JavaPlugin {

    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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
        Bukkit.getLogger().warning(LicenseManager.NOTICE);

        getCommand("smartqueue").setExecutor(new LicenseCommand());
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channel.METHODS);
        getServer().getMessenger().registerIncomingPluginChannel(this, Channel.METHODS, MessageManager.getInstance());
        getServer().getServicesManager().register(SmartQueue.class, SQ.getInstance(), this, ServicePriority.High);

        // Add action for DeluxeHub
        if(getServer().getPluginManager().isPluginEnabled("DeluxeHub")) {
            JoinAction.addToManager();
        }

        // Add trait for Citizens
        if (getServer().getPluginManager().isPluginEnabled("Citizens")) {
            JoinQueueTrait.register();
        }

        new Metrics(this, 10331);
    }

    public static MainSpigot getInstance() {
        return instance;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
