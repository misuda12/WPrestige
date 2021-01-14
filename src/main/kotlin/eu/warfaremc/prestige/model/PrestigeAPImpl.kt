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

import eu.warfaremc.prestige.api.PrestigeAPI
import eu.warfaremc.prestige.kguava
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.collections.ArrayList

internal class PrestigeAPImpl(val prestige: eu.warfaremc.prestige.PrestigeAddon) : PrestigeAPI {
    override fun addPrestige(uniqueId: String?): Int {
        if (uniqueId.isNullOrEmpty())
            return 0
        val number = getPrestige(uniqueId) + 1
        kguava.put(uniqueId, number)
        transaction(prestige.database) {
            Prestiges.update({ Prestiges.id eq uniqueId }) {
                with(SqlExpressionBuilder) {
                    it[level] = level + 1
                }
            }
        }
        return number
    }

    override fun setPrestige(uniqueId: String?, number: Int) {
        if (uniqueId.isNullOrEmpty())
            return
        val entry = kguava.getIfPresent(uniqueId)
        if (entry != null && entry as Int == number || number < 1)
            return
        kguava.put(uniqueId, number)
        transaction(prestige.database) {
            val sql = "INSERT OR REPLACE INTO `t_prestiges` VALUES (?, ?)"
            val statement = TransactionManager.current().connection.prepareStatement(sql, false)
                statement.fillParameters(listOf(
                    Pair(VarCharColumnType(), uniqueId),
                    Pair(IntegerColumnType(), number)
                ))
            statement.executeQuery()
        }
    }

    override fun getPrestige(uniqueId: String?): Int {
        if (uniqueId.isNullOrEmpty())
            return 0
        val entry = kguava.getIfPresent(uniqueId)
        if (entry != null)
            return entry as Int
        var result = 0
        transaction(prestige.database) {
            result = Prestiges.select { Prestiges.id eq uniqueId }.singleOrNull()?.get(Prestiges.level) ?: 0
        }
        return result
    }

    override fun getAll(): MutableList<Prestige> {
        val prestiges: ArrayList<Prestige> = arrayListOf()
        transaction(prestige.database) {
            Prestiges.selectAll().map {
                prestiges.add(
                    Prestige(
                        it[Prestiges.id],
                        it[Prestiges.level]
                    )
                )
            }
        }
        return prestiges
    }

    override fun getCurrentPrestige(uniqueId: String?): Int = getPrestige(uniqueId)

    override fun exists(uniqueId: String?): Boolean {
        if (uniqueId.isNullOrEmpty())
            return false
        if (kguava.getIfPresent(uniqueId) != null)
            return true
        var result = false
        transaction(prestige.database) {
            result = Prestiges.select { Prestiges.id eq uniqueId }.any()
        }
        return result
    }

    override fun remove(uniqueId: String?): Prestige? {
        if (uniqueId.isNullOrEmpty())
            return null
        val entry = kguava.getIfPresent(uniqueId)
        transaction {
            Prestiges.deleteWhere { Prestiges.id eq uniqueId }
        }
        if (entry != null)
            return Prestige(uniqueId, entry as Int)
        return null
    }

    override fun save(uniqueId: String?) {
        if (uniqueId.isNullOrEmpty())
            return
        val number = kguava.getIfPresent(uniqueId) ?: return
        transaction(prestige.database) {
            val sql = "INSERT OR REPLACE INTO `t_prestiges` VALUES (?, ?)"
            val statement = TransactionManager.current().connection.prepareStatement(sql, false)
            statement.fillParameters(listOf(
                Pair(VarCharColumnType(), uniqueId),
                Pair(IntegerColumnType(), number)
            ))
            statement.executeQuery()
        }
    }
}