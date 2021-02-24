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

package ch.swisscypher.smartqueue.bungee.config;

import ch.swisscypher.smartqueue.bungee.MainBungee;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class Lang {

    private Configuration configuration;

    public Lang(String langFile) {
        File f = new File(MainBungee.getInstance().getDataFolder(), "lang/" + langFile);
        File d = MainBungee.getInstance().getDataFolder();
        File lang = new File(d, "lang");

        if(!lang.exists()) {
            lang.mkdir();
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(f);
        } catch (IOException e1) {
            File fen = new File(MainBungee.getInstance().getDataFolder(), "lang/en.yml");
            try {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
                        .load(fen);
            } catch (IOException e2) {
                try {
                    fen.createNewFile();
                    InputStream is = getClass().getResourceAsStream("/config/lang/en.yml");
                    OutputStream os = new FileOutputStream(fen);
                    ByteStreams.copy(is, os);

                    configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
                            .load(fen);
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
