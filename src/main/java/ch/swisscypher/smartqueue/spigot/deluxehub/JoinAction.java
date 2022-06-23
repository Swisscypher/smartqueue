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

package ch.swisscypher.smartqueue.spigot.deluxehub;

import ch.swisscypher.smartqueue.spigot.MainSpigot;
import ch.swisscypher.smartqueue.spigot.SQ;
import fun.lewisdev.deluxehub.DeluxeHubPlugin;
import fun.lewisdev.deluxehub.action.Action;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinAction implements Action {

    private JoinAction() { }
    private static JoinAction instance;

    @Override
    public String getIdentifier() {
        return "SMARTQUEUE";
    }

    @Override
    public void execute(DeluxeHubPlugin deluxeHubPlugin, Player player, String s) {
        MainSpigot.getInstance().getLogger().info(String.format("Received join action from DeluxeMenu for player %s on queue %s", player.getName(), s));
        SQ.getInstance().addPlayer(player, s);
    }

    public static void addToManager() {
        if(instance == null) {
            instance = new JoinAction();
            JavaPlugin.getPlugin(DeluxeHubPlugin.class).getActionManager().registerAction(instance);
        }
    }
}
