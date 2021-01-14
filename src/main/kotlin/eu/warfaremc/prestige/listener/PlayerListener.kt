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
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import world.bentobox.bentobox.api.events.island.IslandDeletedEvent
import world.bentobox.bentobox.api.events.island.IslandResetEvent
import world.bentobox.bentobox.api.events.team.TeamJoinedEvent as TeamJoinEvent
import world.bentobox.bentobox.api.events.team.TeamKickEvent
import world.bentobox.bentobox.api.events.team.TeamLeaveEvent  as TeamQuitEvent

class PlayerListener : Listener {

    @EventHandler
    fun PlayerJoinEvent.on() {
        if (api.exists(player.uniqueId).not()) {
            api.setPrestige(player.uniqueId, 0)
            player.sendMessage("§a§l(!) §aProfil ve §7WarfarePrestiges §aúspěšně vytvořen.")
            return
        }
        player.sendMessage("§b§l(!) §bNačítání dat WarfarePrestiges...")
    }

    @EventHandler
    fun PlayerQuitEvent.on() {
        if (api.exists(player.uniqueId)) {
            api.save(player.uniqueId)
            kguava.invalidate(player.uniqueId)
        }
    }

    @EventHandler
    fun TeamJoinEvent.on() {
        api.setPrestige(playerUUID, api.getPrestige(owner))
        val player = addon.server.getPlayer(playerUUID) ?: return
        player.sendMessage("§b§l(!) §bPrestige byla synchronizována.")
    }

    @EventHandler
    fun TeamQuitEvent.on() {
        api.remove(playerUUID)
        val player = addon.server.getPlayer(playerUUID) ?: return
        player.sendMessage("§b§l(!) §bPrestige byla nastavena na §70§b, jelikož jsi opustil ostrov.")
    }

    @EventHandler
    fun TeamKickEvent.on() {
        api.remove(playerUUID)
        val player = addon.server.getPlayer(playerUUID) ?: return
        player.sendMessage("§b§l(!) §bPrestige byla nastavena na §70§b, jelikož jsi opustil ostrov.")
    }

    @EventHandler
    fun IslandResetEvent.on() {
        api.remove(playerUUID)
        val player = addon.server.getPlayer(playerUUID) ?: return
        player.sendMessage("§b§l(!) §bPrestige byla nastavena na §70§b, jelikož jsi opustil ostrov.")
    }

    @EventHandler
    fun IslandDeletedEvent.on() {
        api.remove(playerUUID)
        val player = addon.server.getPlayer(playerUUID) ?: return
        player.sendMessage("§b§l(!) §bPrestige byla nastavena na §70§b, jelikož jsi opustil ostrov.")
    }
}