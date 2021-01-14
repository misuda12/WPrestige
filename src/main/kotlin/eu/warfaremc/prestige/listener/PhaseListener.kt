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
import eu.warfaremc.prestige.miscellanneous.findNameByIsland
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import world.bentobox.aoneblock.events.MagicBlockPhaseEvent

class PhaseListener : Listener {

    @EventHandler
    fun MagicBlockPhaseEvent.on() {
        if (phase == "Plains") {
            if (island.owner == null)
                return
            if (!listOf(0, 11000, 11001).contains(blockNumber))
                return

            val number = api.addPrestige(island.uniqueId)
            val name = findNameByIsland(island);

            addon.server.broadcastMessage("§a§l(!) §aOstrov§7 $name §azískal Prestige §7$number")
        }
    }
}