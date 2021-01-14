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

package eu.warfaremc.prestige.command

import eu.warfaremc.prestige.addon
import eu.warfaremc.prestige.api
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import world.bentobox.bentobox.api.addons.Addon
import world.bentobox.bentobox.api.commands.CompositeCommand
import world.bentobox.bentobox.api.user.User


class PClaimCommand(addon: Addon, parent: CompositeCommand, label: String) : CompositeCommand(addon, parent, label) {
    override fun execute(user: User?, label: String?, args: MutableList<String>?): Boolean {
        if (user != null) {
            if (api.exists(user.uniqueId)) {
                val number = api.getPrestige(user.uniqueId)
                when {
                    number >= 2  -> {
                        if (user.hasPermission("prestige.2"))
                            return false
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set prestige.2")
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set cmi.command.warp.pvp")
                        user.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7/warp pvp")
                    }
                    number >= 3  -> {
                        if (user.hasPermission("prestige.3.new"))
                            return false
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set prestige.3.new")
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set enchantgui.enchant")
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set oregen.p3")
                        user.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7Magický Enchant§a. §7/warp enchant")
                        user.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7MAGIC COBBLESTONE GENERATOR T1§a.")
                    }
                    number >= 4 || number >= 5 || number >= 6 || number >= 7 || number >= 8 || number >= 9 -> {
                        if (user.hasPermission("prestige.4"))
                            return false
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set prestige.$number")
                        user.inventory?.addItem(ItemStack(Material.NETHER_STAR, 2))
                        user.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aZískal jsi §72x Nether Star")
                    }
                    number >= 10 -> {
                        if (user.hasPermission("prestige.10"))
                            return false
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set prestige.10")
                        addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${user.name} perm set deluxetags.tag.Trihard")
                        user.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7Trihard Tag")
                    }
                }
            }
            return true
        }
        return false
    }

    override fun setup() {
        isOnlyPlayer = true
        description  = "Claims your reward for prestige"
        permission   = "wp.claim"
    }
}