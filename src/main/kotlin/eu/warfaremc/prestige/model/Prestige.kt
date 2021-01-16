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

import com.google.gson.annotations.Expose
import org.jetbrains.exposed.sql.Table
import world.bentobox.bentobox.database.objects.DataObject
import world.bentobox.bentobox.database.objects.Table as BBTable
import java.io.Serializable

object Prestiges : Table("t_prestiges") {

    val id = varchar("id", 64)
    val level = integer("level").default(0)
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PKPrestigesID")
}

@BBTable(name = "PrestigeReward")
class PrestigeReward(@JvmField @Expose var uniqueId: String) : DataObject {

    @Expose
    var prestige: Int = 0

    @Expose
    var rewarded: Int = 0

    override fun getUniqueId(): String
        = uniqueId

    override fun setUniqueId(uniqueId: String) {
        this.uniqueId = uniqueId
    }
}

data class Prestige(
    val id: String,
    val data: Int = 0
) : Serializable

data class PrestigeDAO(
    val id: String,
    var data: Int = 0
) : Serializable