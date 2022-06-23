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

package ch.swisscypher.smartqueue.spigot.citizens;

import ch.swisscypher.smartqueue.spigot.MainSpigot;
import ch.swisscypher.smartqueue.spigot.SQ;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.command.CommandConfigurable;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.exception.CommandException;
import net.citizensnpcs.api.command.exception.CommandUsageException;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.event.EventHandler;

public class JoinQueueTrait extends Trait implements CommandConfigurable {
    @Persist("queue")
    private String queueName;

    @Persist("isOnLeftClick")
    private boolean isOnLeftClick = false;

    @Persist("isOnRightClick")
    private boolean isOnRightClick = true;

    public JoinQueueTrait() {
        super("joinqueue");
    }

    public static void register() {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(JoinQueueTrait.class).withName("joinqueue"));
    }

    @EventHandler
    public void onNPCLeftClick(NPCLeftClickEvent event) {
        if (event.getNPC() != getNPC() || !isOnLeftClick) {
            return;
        }

        SQ.getInstance().addPlayer(event.getClicker(), queueName);
        MainSpigot.getInstance().getLogger().info(String.format("Received interaction from Citizens for player %s on queue %s", event.getClicker().getName(), queueName));
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        if (event.getNPC() != this.getNPC() || !isOnRightClick) {
            return;
        }

        SQ.getInstance().addPlayer(event.getClicker(), queueName);
        MainSpigot.getInstance().getLogger().info(String.format("Received interaction from Citizens for player %s on queue %s", event.getClicker().getName(), queueName));
    }

    @Override
    public void configure(CommandContext commandContext) throws CommandException {
        if (!commandContext.hasValueFlag("queue") && queueName == null) {
            throw new CommandUsageException("Queue flag is required", "/traitc joinqueue --queue <queue> [-l] [-r]");
        }

        queueName = commandContext.getFlag("queue");

        isOnLeftClick = commandContext.hasFlag('l');
        isOnRightClick = commandContext.hasFlag('r');

        MainSpigot.getInstance().getLogger().info(String.format("JoinQueueTrait configured for NPC %s, queue: %s, left click: %s, right click: %s", getNPC().getName(), queueName, isOnLeftClick, isOnRightClick));
    }
}
