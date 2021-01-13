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

package eu.warfaremc.prestige.model

import eu.warfaremc.prestige.addons
import eu.warfaremc.prestige.api
import eu.warfaremc.prestige.api.PrestigeAPI
import eu.warfaremc.prestige.miscellanneous.toRoman
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

object PrestigePlaceholder : PlaceholderExpansion() {

    val prestigeAPI: PrestigeAPI by lazy { api }

    override fun getIdentifier(): String
            = "wf"

    override fun getAuthor(): String
            = addons.description.authors.joinToString(", ")

    override fun getVersion(): String
            = addons.description.version

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        if (player == null)
            return ""
        if (identifier == "prestige") {
            return if (prestigeAPI.exists(player.uniqueId) && prestigeAPI.getPrestige(player.uniqueId) == 0) "0"
            else toRoman(prestigeAPI.getPrestige(player.uniqueId)) ?: ""
        }
        return super.onPlaceholderRequest(player, params)
    }
}