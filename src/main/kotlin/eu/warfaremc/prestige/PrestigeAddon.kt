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

package eu.warfaremc.prestige

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import eu.warfaremc.prestige.api.PrestigeAPI
import eu.warfaremc.prestige.command.PClaimCommand
import eu.warfaremc.prestige.command.SetPCommand
import eu.warfaremc.prestige.command.SyncPCommand
import eu.warfaremc.prestige.command.TopCommand
import eu.warfaremc.prestige.ui.RankUI
import eu.warfaremc.prestige.listener.PhaseListener
import eu.warfaremc.prestige.listener.PlayerListener
import eu.warfaremc.prestige.model.PrestigeAPImpl
import eu.warfaremc.prestige.model.PrestigePlaceholder
import eu.warfaremc.prestige.model.Prestiges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import mu.KotlinLogging
import org.bukkit.configuration.file.FileConfiguration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import world.bentobox.bentobox.api.addons.Addon

@PublishedApi
internal lateinit var addon: PrestigeAddon
    private set

@PublishedApi
internal lateinit var kguava: Cache<Any, Any>
    private set

@PublishedApi
internal lateinit var api: PrestigeAPI
class PrestigeAddon : Addon(), CoroutineScope by MainScope() {

    val logger by lazy { KotlinLogging.logger("WPrestiges") }
    internal val session = UUID.randomUUID().toString()
    private val separate = File.separator

    @PublishedApi
    internal var fisql: Database? = null
        private set
    internal var mesql: Database? = null
        private set

    @PublishedApi
    internal val database: Database get() {
        if (fisql != null)
            return fisql!!
        if (mesql != null)
            return mesql!!
        mesql = Database.connect(
            url = "jdbc:sqlite::memory:",
            user = "proxy",
            password = session,
            driver = "org.sqlite.JDBC"
        )
        logger.info("Connected to ${mesql!!.url}, productName: ${mesql!!.vendor}, " +
                "productVersion: ${mesql!!.version}, logger: $logger, dialect: ${mesql!!.dialect}")
        return mesql!!
    }

    init {
        kguava = CacheBuilder.newBuilder()
            .expireAfterWrite(Long.MAX_VALUE, TimeUnit.DAYS)                                                            // Never expirable cache
            .build()

        mesql = Database.connect(
            url = "jdbc:sqlite::memory:",
            user = "proxy",
            password = session,
            driver = "org.sqlite.JDBC"
        )

        api = PrestigeAPImpl(this)
    }

    override fun onLoad() {
        if (!dataFolder.exists())
            dataFolder.mkdir().also { logger.info { "[IO] dataFolder ~'${dataFolder.path}' created" } }

        super.onLoad()
    }
    override fun onEnable() {
        if (::api.isInitialized == false)
            api = PrestigeAPImpl(this)
        val fisqlResult = kotlin.runCatching {
            fisql = Database.connect(
                url = "jdbc:sqlite:$dataFolder${File.separator}prestige.db",
                driver = "org.sqlite.JDBC"
            )
            logger.info("Connected to ${fisql!!.url}, productName: ${fisql!!.vendor}, " +
                    "productVersion: ${fisql!!.version}, logger: $logger, dialect: ${fisql!!.dialect}")
        }
        if (fisqlResult.isFailure)
            logger.error { "Failed to initialize database: 'fisql@jdbc:sqlite:$dataFolder${File.separator}prestige.db'" }
                .also { logger.error { fisqlResult.exceptionOrNull() } }
        transaction(database) {
            SchemaUtils.create(Prestiges)
        }
        logger.warn { "Using primary database: '${database.url}, productName: ${database.vendor}, " +
                "productVersion: ${database.version}, logger: $logger, dialect: ${database.dialect}'" }

        registerListener(PhaseListener ())
        registerListener(PlayerListener())
        registerListener(RankUI())
        if (server.pluginManager.getPlugin("PlaceholderAPI") != null)
            PrestigePlaceholder.register()
        plugin.addonsManager.gameModeAddons.forEach { addon ->
            addon.playerCommand.ifPresent { PClaimCommand(this, it, "pclaim") }
            addon.adminCommand .ifPresent { SetPCommand(this, it, "setp") }
            addon.playerCommand.ifPresent { SyncPCommand(this, it, "syncp") }
            addon.playerCommand.ifPresent { TopCommand(this, it, "top") }
        }
    }

    override fun onDisable() {  }
}