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

package eu.warfaremc.prestige.ui

import eu.warfaremc.prestige.addon
import eu.warfaremc.prestige.api
import eu.warfaremc.prestige.miscellanneous.toRoman
import eu.warfaremc.prestige.model.extension.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.ItemMeta
import world.bentobox.aoneblock.AOneBlock
import world.bentobox.bentobox.BentoBox
import world.bentobox.bentobox.database.objects.Island
import java.util.*

class RankUI : Listener {
    companion object {


        val rankMap = hashMapOf<Int, String>()

        fun openParticlesMenu(player: Player?) {

            val islands = hashMapOf<Island, Int>()
            val inventory = Bukkit.createInventory(null, 54, "§b§l(!) §bOneBlock Top")
            val oneblockInstance = addon.plugin.addonsManager.getAddonByName<AOneBlock>("AOneBlock").get()

            BentoBox.getInstance().islands.islands.forEach { island ->
                val prestige = api.getPrestige(UUID.fromString(island.uniqueId))

                if(prestige > 0)
                    islands[island] = oneblockInstance.getOneBlocksIsland(island).blockNumber + ((api.getPrestige(UUID.fromString(island.uniqueId)) - 1) * 11000)
                else
                    islands[island] = oneblockInstance.getOneBlocksIsland(island).blockNumber
            }

            val sortedMap = islands.toSortedMap { i0, i1 ->
                api.getPrestige(UUID.fromString(i0.uniqueId)) - api.getPrestige(UUID.fromString(i1.uniqueId))
            }

            var index = 0
            val positions = arrayOf(13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43).iterator()

            sortedMap.entries.forEach { entry ->
                if(!positions.hasNext())
                    return

                val material: Material = when {
                    index == 0 -> Material.LIGHT_BLUE_STAINED_GLASS_PANE
                    index <= 3 -> Material.YELLOW_STAINED_GLASS_PANE
                    index <= 8 -> Material.ORANGE_STAINED_GLASS_PANE
                    else -> Material.RED_STAINED_GLASS_PANE
                }

                if(entry.key.owner == null)
                    return

                val topicon = item(material) {
                    meta<ItemMeta> {
                        name = "§b§l(!) §b ${Bukkit.getOfflinePlayer(UUID.fromString(entry.key.owner.toString())).name}"

                        stringLore = """
                            
                            §b    §b⚹ Místo: §7." + ${index + 1}
                            §b    §b⚹ Vytěženo bloků: §7${entry.value}
                            §b    §b⚹ Prestige: §7${toRoman(api.getPrestige(UUID.fromString(entry.key.uniqueId)))}
                            
                            §b§l(!) §7§nNavštívit ostrov
                        """.trimIndent()
                    }
                }

                inventory.setItem(positions.next(), topicon)
                index++
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
                addon.server.dispatchCommand(whoClicked, "ob warp ${rankMap[slot]}")
        }
    }
}