/*
 * This file is part of WarfareMC, licensed under the MIT License.
 *
 * Copyright (C) 2020 WarfareMC & Team
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

import eu.warfaremc.prestige.addon
import eu.warfaremc.prestige.api
import eu.warfaremc.prestige.kguava
import eu.warfaremc.prestige.miscellanneous.findIslandByPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import world.bentobox.bentobox.api.events.island.IslandCreatedEvent
import world.bentobox.bentobox.api.events.island.IslandDeleteEvent
import world.bentobox.bentobox.api.events.island.IslandResetEvent

class PlayerListener : Listener {

    @EventHandler
    fun PlayerQuitEvent.on() {
        val island = findIslandByPlayer(player.uniqueId)?.uniqueId ?: return
        if (api.exists(island)) {
            api.save(island)
            kguava.invalidate(player.uniqueId)
        }
    }

    @EventHandler
    fun IslandCreatedEvent.on() {
        if (api.exists(island.uniqueId) == false) {
            api.setPrestige(island.uniqueId, 1)
        }
    }

    @EventHandler
    fun IslandResetEvent.on() {
        api.remove(island.uniqueId)
        island.memberSet.mapNotNull { addon.server.getPlayer(it) }
            .filter { it.isOnline }
            .forEach {
                it.sendMessage("§b§l(!) §bPrestige byla nastavena na §70§b, jelikož ostrov byl zmazán.")
            }
    }

    @EventHandler
    fun IslandDeleteEvent.on() {
        if (island == null)
            return
        api.remove(island.uniqueId)
        island.memberSet.mapNotNull { addon.server.getPlayer(it) }
            .filter { it.isOnline }
            .forEach {
                it.sendMessage("§b§l(!) §bPrestige byla nastavena na §70§b, jelikož ostrov byl zmazán.")
            }
    }
}