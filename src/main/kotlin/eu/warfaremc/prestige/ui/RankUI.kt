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
import eu.warfaremc.prestige.miscellanneous.findNameByIsland
import eu.warfaremc.prestige.miscellanneous.toRoman
import eu.warfaremc.prestige.model.PrestigeDAO
import eu.warfaremc.prestige.model.Prestiges
import eu.warfaremc.prestige.model.extension.*
import eu.warfaremc.prestige.oneblock
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import world.bentobox.bentobox.database.objects.Island

class RankUI : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun InventoryClickEvent.on() {
        if (view.title == "§b§l(!) §bOneBlock Top")
            isCancelled = true
    }
}

fun openParticlesMenu(player: Player?) {
    if (player == null)
        return
    val inventory = inventory(rows = 6, title = "§b§l(!) §bOneBlock Top") {
        val triangle = listOf(13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43)
        val border = item(Material.BLACK_STAINED_GLASS_PANE) {
            meta<ItemMeta> {
                name = " "
            }
        }
        all.forEach { this[it] = border }
        var list: List<Pair<Island, PrestigeDAO>> = arrayListOf()
        transaction(addon.database) {
            list = Prestiges.selectAll().orderBy(Prestiges.id, SortOrder.DESC)
                .asSequence()
                .map { PrestigeDAO(it[Prestiges.id], it[Prestiges.level]) }
                .map { Pair(oneblock.islands.getIslandById(it.id), it) }
                .filter { it.first.isPresent }
                .map { Pair(it.first.get(), it.second) }
                .onEach {
                    it.second.data = oneblock.getOneBlocksIsland(it.first).blockNumber + ((it.second.data) - 1) * 11000
                }
                .sortedByDescending { it.second.data }
                .toList()
        }
        triangle.forEachIndexed { index, position ->
            val result: Result<Pair<Island, PrestigeDAO>> = kotlin.runCatching {
                list[index]
            }
            if (result.isSuccess) {
                val material: Material = when {
                    index == 0 -> Material.LIGHT_BLUE_STAINED_GLASS_PANE
                    index <= 3 -> Material.YELLOW_STAINED_GLASS_PANE
                    index <= 8 -> Material.ORANGE_STAINED_GLASS_PANE
                    else -> Material.RED_STAINED_GLASS_PANE
                }
                val item = item(material) {
                    meta<ItemMeta> {
                        val owner = findNameByIsland(result.getOrNull()!!.first)
                        name = "§b§l(!) §b $owner"
                        stringLore = """
                        
                                    §b    §b⚹ Místo: §7${index + 1}
                                    §b    §b⚹ Vytěženo bloků: §7${result.getOrNull()!!.second.data}
                                    §b    §b⚹ Prestige: §7${toRoman(api.getPrestige(result.getOrNull()!!.second.id))}
                                    
                                """.trimIndent()
                    }
                }
                this[position] = item
            }
        }
    }

    // Escaping from Command.ASYNC to Server.SYNC_BLOCKING
    addon.server.scheduler.callSyncMethod(addon.plugin) { inventory.openTo(player) }
}