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

import eu.warfaremc.prestige.ui.RankUI
import world.bentobox.bentobox.api.addons.Addon
import world.bentobox.bentobox.api.commands.CompositeCommand
import world.bentobox.bentobox.api.user.User

class TopCommand(addon: Addon, parent: CompositeCommand, label: String) : CompositeCommand(addon, parent, label) {
    override fun execute(user: User?, label: String?, args: MutableList<String>?): Boolean {

        if (user != null) {
            user.sendMessage("Opening menu")
            RankUI.openParticlesMenu(user.player)
            return true
        }
        return false
    }

    override fun setup() {
        isOnlyPlayer = true
        description  = "Shows top islands ordered by block mined"
        permission   = "wp.top"
    }
}