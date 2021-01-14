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

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.Regex
import cloud.commandframework.annotations.specifier.Greedy
import eu.warfaremc.prestige.addon
import eu.warfaremc.prestige.api
import eu.warfaremc.prestige.miscellanneous.findIslandByPlayer
import eu.warfaremc.prestige.ui.RankUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull

class CommandResolver {
    init {
        addon.commandAnnotation.parse(this)
    }

    @CommandMethod("prestige get")
    @CommandDescription("Claims island reward for prestige level")
    @CommandPermission ("wp.claim")
    fun commandGet(
        @NotNull sender: Player,
    ) {
        val island = findIslandByPlayer(sender.uniqueId)
        if(island == null) {
            sender.sendMessage("[WPrestige] Player '${sender.name}' is not not apart of any island")
            return
        }
        if (api.exists(island.uniqueId)) {
            val number = api.getPrestige(island.uniqueId)
            when {
                number >= 2  -> {
                    if (sender.hasPermission("prestige.2"))
                        return
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.2")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set cmi.command.warp.pvp")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7/warp pvp")
                }
                number >= 3  -> {
                    if (sender.hasPermission("prestige.3.new"))
                        return
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.3.new")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set enchantgui.enchant")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set oregen.p3")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7Magický Enchant§a. §7/warp enchant")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7MAGIC COBBLESTONE GENERATOR T1§a.")
                }
                number >= 4 || number >= 5 || number >= 6 || number >= 7 || number >= 8 || number >= 9 -> {
                    if (sender.hasPermission("prestige.4"))
                        return
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.$number")
                    sender.inventory?.addItem(ItemStack(Material.NETHER_STAR, 2))
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aZískal jsi §72x Nether Star")
                }
                number >= 10 -> {
                    if (sender.hasPermission("prestige.10"))
                        return
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.10")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set deluxetags.tag.Trihard")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7Trihard Tag")
                }
            }
        }
    }

    @CommandMethod("prestige set <player> <number>", requiredSender = CommandSender::class)
    @CommandDescription("Sets prestige level number if present")
    @CommandPermission ("wp.admin.set")
    fun commandSet(
        @NotNull sender: CommandSender,
        @NotNull @Argument("player") player: String,
        @NotNull @Argument("number") @Regex("^([0-9]|[1-9][0-9]|[1-9][0-9][0-9])\$", failureCaption = "0") number: Int
    ) {
        val target = addon.server.getPlayer(player)
        if (target == null) {
            sender.sendMessage("[WPrestige] Player does not exists")
            return
        }
        val island = findIslandByPlayer(target.uniqueId)
        if(island == null) {
            sender.sendMessage("[WPrestige] Player '${player}' is not not apart of any island")
            return
        }
        api.setPrestige(island.uniqueId, number)
        sender.sendMessage("[WPrestige] Prestige set to $number for island: ${target.name}")
    }

    @CommandMethod("prestige top", requiredSender = CommandSender::class)
    @CommandDescription("Shows islands by their prestige levels")
    @CommandPermission ("wp.top")
    fun commandTop(
        @NotNull sender: Player,
    ) {
        RankUI.openParticlesMenu(sender)
    }

    @CommandMethod("prestige help [query]")
    @CommandDescription("Shows commands of WPrestige")
    fun commandHelp(
        @NotNull sender: CommandSender,
        @Argument("query") @Greedy query: String?
    ) {
        addon.commandHelp.queryCommands(query ?: "", sender)
    }
}