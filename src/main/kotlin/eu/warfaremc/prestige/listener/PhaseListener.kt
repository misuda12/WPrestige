/*
 * This file is part of Millennium, licensed under the MIT License.
 *
 * Copyright (C) 2020 Millennium & Team
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.warfaremc.prestige.listener

import eu.warfaremc.prestige.addons
import eu.warfaremc.prestige.api
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import world.bentobox.aoneblock.events.MagicBlockEvent
import world.bentobox.aoneblock.events.MagicBlockPhaseEvent

class PhaseListener : Listener {

    @EventHandler
    fun MagicBlockPhaseEvent.on() {
        if (phase == "Plains") {
            if (island.owner == null)
                return
            if (!listOf(0, 11000, 11001).contains(blockNumber))
                return
            val player = addons.server.getPlayer(island.owner!!) ?: return
            val number = api.addPrestige(island.owner)
            player.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aTvůj ostrov dosáhl nové §7Prestige")
            addons.server.broadcastMessage("§a§l(!) §aOstrov §7${island.name} §a získal Prestige §7$number")
        }
    }

    @EventHandler
    fun MagicBlockEvent.on() {
        if (playerUUID == null)
            return
        val number = api.getPrestige(playerUUID)
        if (number != 0 && number > 0)
            return
        val player = addons.server.getPlayer(island.owner!!) ?: return
        if (Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%aoneblock_my_island_count%")) > 1) {
            api.setPrestige(playerUUID, 1)
            player.sendMessage("§e§l(?) §7§nOPRAVA!§e §eAutomaticky jsme opravili tvůj Prestige na §7I")
        }
    }
}