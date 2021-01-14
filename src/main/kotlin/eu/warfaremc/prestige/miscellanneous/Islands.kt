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

package eu.warfaremc.prestige.miscellanneous

import eu.warfaremc.prestige.addon
import eu.warfaremc.prestige.bentobox
import world.bentobox.bentobox.database.objects.Island
import java.util.*

fun findIslandByPlayer(uniqueId: UUID?): Island? {
    if (uniqueId == null)
        return null
    return bentobox.islands.islands.firstOrNull {
        it.memberSet.contains(uniqueId)
                || (it.owner != null && it.owner!! == uniqueId)
    }
}

fun findNameByIsland(island: Island?): String {
    if (island == null)
        return "unknown"
    if (island.name != null)
        return island.name!!
    val player = island.owner?.let { addon.server.getPlayer(it) } ?: return "unknown"
    return player.name
}