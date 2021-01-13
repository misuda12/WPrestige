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

import eu.warfaremc.prestige.addons
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import world.bentobox.bentobox.database.objects.Island

class GUI : Listener {
    companion object {
        fun openParticlesMenu(player: Player?) {
            try {
                val islands = hashMapOf<Island, Int>()
                TODO("Finish this @WattMann")
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    @EventHandler
    fun InventoryClickEvent.on() {
        if (view.title == "§b§l(!) §bOneBlock Top") {
            isCancelled = true
            if (currentItem == null)
                return
            if (currentItem!!.type == Material.AIR)
                return
            if (currentItem!!.type.toString().contains("stained_glass_pane"))
                addons.server.dispatchCommand(Bukkit.getConsoleSender(), "ob warp " + currentItem?.itemMeta?.displayName?.replace("§b§l(!) §b", ""))
        }
    }
}