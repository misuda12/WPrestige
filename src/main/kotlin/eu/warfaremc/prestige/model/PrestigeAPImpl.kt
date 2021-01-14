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
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.collections.ArrayList

internal class PrestigeAPImpl(val prestige: eu.warfaremc.prestige.PrestigeAddon) : PrestigeAPI {
    override fun addPrestige(uniqueId: UUID?): Int {
        if (uniqueId == null)
            return 0
        val number = getPrestige(uniqueId) + 1
        kguava.put(uniqueId, number)
        transaction(prestige.database) {
            if (exists(uniqueId)) {
                Prestiges.update({ Prestiges.id eq uniqueId }) {
                    with(SqlExpressionBuilder) {
                        it[data] = data + 1                                                                                 // TODO: Possible inconsistency ?
                    }
                }
                return@transaction
            }
            Prestiges.insertIgnore {
                it[id] = uniqueId
                it[data] = number
            }
        }
        return number
    }

    override fun setPrestige(uniqueId: UUID?, number: Int, save: Boolean) {
        if (uniqueId == null)
            return
        val entry = kguava.getIfPresent(uniqueId)
        if (entry != null && entry as Int == number || number < 0)
            return
        kguava.put(uniqueId, number)
        if (save) transaction(prestige.database) {
            if (exists(uniqueId)) {
                Prestiges.update({ Prestiges.id eq uniqueId }) {
                    it[data] = number
                }
            }
            Prestiges.insertUpdate(Prestige(uniqueId, number)) { insert, it ->
                insert[id]   = it.id
                insert[data] = it.data
            }
        }
    }

    override fun getPrestige(uniqueId: UUID?): Int {
        if (uniqueId == null)
            return 0
        val entry = kguava.getIfPresent(uniqueId)
        if (entry != null)
            return entry as Int
        var result = 0
        transaction(prestige.database) {
            result = Prestiges.select { Prestiges.id eq uniqueId }.singleOrNull()?.get(Prestiges.data) ?: 0
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
                        it[Prestiges.data]
                    )
                )
            }
        }
        return prestiges
    }

    override fun exists(uniqueId: UUID?): Boolean {
        if (uniqueId == null)
            return false
        if (kguava.getIfPresent(uniqueId) != null)
            return true
        var result = false
        transaction(prestige.database) {
            result = Prestiges.select { Prestiges.id eq uniqueId }.any()
        }
        return result
    }

    override fun remove(uniqueId: UUID?): Prestige? {
        if (uniqueId == null)
            return null
        val entry = kguava.getIfPresent(uniqueId)
        transaction {
            Prestiges.deleteWhere { Prestiges.id eq uniqueId }
        }
        if (entry != null)
            return Prestige(uniqueId, entry as Int)
        return null
    }

    override fun save(uniqueId: UUID?) {
        if (uniqueId == null)
            return
        val entry = kguava.getIfPresent(uniqueId) ?: return
        transaction(prestige.database) {
            if (exists(uniqueId)) {
                Prestiges.update({ Prestiges.id eq uniqueId }) {
                    it[data] = entry as Int
                }
            }
            Prestiges.insertUpdate(Prestige(uniqueId, entry as Int)) { insert, it ->
                insert[id]   = it.id
                insert[data] = it.data
            }
        }
    }

    override fun save(bulk: MutableMap<UUID?, Int>) {
        val prestiges = bulk.filter { it.key != null }
            .onEach { kguava.put(it.key!!, it.value) }
            .map { Prestige(it.key!!, it.value) }
        transaction(prestige.database) {
            prestiges.forEach { prestige ->
                if (exists(prestige.id)) {
                    Prestiges.update({ Prestiges.id eq prestige.id }) {
                        it[data] = prestige.data
                    }
                }
                Prestiges.insertUpdate(Prestige(prestige.id, prestige.data)) { insert, it ->
                    insert[id]   = it.id
                    insert[data] = it.data
                }
            }
            Prestiges.batchInsertUpdate(prestiges, listOf(Prestiges.id, Prestiges.data)) { batch, it ->
                batch[id]   = it.id
                batch[data] = it.data
            }
        }
    }
}