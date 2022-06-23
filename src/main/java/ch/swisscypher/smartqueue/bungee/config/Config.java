/*
 * SmartQueue: Minecraft plugin implementing a queue system.
 * Copyright (C) 2021-2022 SwissCypher (contact@swisscypher.ch)
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

package ch.swisscypher.smartqueue.bungee.config;

import ch.swisscypher.smartqueue.bungee.MainBungee;
import ch.swisscypher.smartqueue.bungee.exception.QueueNotExistsException;
import ch.swisscypher.smartqueue.bungee.queue.SmartQueueManager;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Config {

    private Configuration configuration;

    private static Config instance;

    public Lang lang;

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }

        return instance;
    }

    private Config() {
        File f = new File(MainBungee.getInstance().getDataFolder(), "config.yml");
        File d = MainBungee.getInstance().getDataFolder();

        if(!d.exists()) {
            d.mkdir();
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(f);
        } catch (IOException e) {
            try {
                f.createNewFile();
                InputStream is = getClass().getResourceAsStream("/config/config.yml");
                OutputStream os = new FileOutputStream(f);
                ByteStreams.copy(is, os);

                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
                        .load(f);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        lang = new Lang(configuration.getString("lang"));
    }

    public String getLabel(String label, Object... values) {
        if(lang.getConfiguration().contains(label)) {
            return String.format(lang.getConfiguration().getString(label), values);
        } else {
            ProxyServer.getInstance().getLogger().severe(String.format("Label %s not found", label));
            return "";
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void load() {
        SmartQueueManager.getInstance().destroyAll();
        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> queues = (List<HashMap<String, Object>>)configuration.getList("queues");
        queues.forEach(q -> {
            ServerInfo destination = ProxyServer.getInstance().getServerInfo((String)q.get("destination"));
            if(destination == null) {
                ProxyServer.getInstance().getLogger().severe(getLabel("destination-non-existent", q.get("destination")));
            } else {
                ProxyServer.getInstance().getLogger().info(String.format("Queue %s added (waiting time %d, does need priority : %b)", q.get("name"), q.get("waiting"), q.get("need-priority")));
                SmartQueueManager.getInstance().createSmartQueue((String)q.get("name"), destination, (Integer)q.get("waiting"), (Boolean)q.get("need-priority"));
            }
        });
    }
}
