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

import cloud.commandframework.annotations.*
import cloud.commandframework.annotations.specifier.Greedy
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import eu.warfaremc.prestige.api.PrestigeAPI
import eu.warfaremc.prestige.ui.RankUI
import eu.warfaremc.prestige.listener.PhaseListener
import eu.warfaremc.prestige.listener.PlayerListener
import eu.warfaremc.prestige.model.PrestigeAPImpl
import eu.warfaremc.prestige.model.PrestigePlaceholder
import eu.warfaremc.prestige.model.Prestiges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import mu.KotlinLogging
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import world.bentobox.aoneblock.AOneBlock
import world.bentobox.bentobox.BentoBox
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import world.bentobox.bentobox.api.addons.Addon
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.kotlin.extension.buildAndRegister
import eu.warfaremc.prestige.miscellanneous.findIslandByPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull


@PublishedApi
internal lateinit var addon: PrestigeAddon
    private set

@PublishedApi
internal lateinit var kguava: Cache<Any, Any>
    private set

@PublishedApi
internal lateinit var api: PrestigeAPI

@PublishedApi
internal lateinit var oneblock: AOneBlock
    private set

@PublishedApi
internal lateinit var bentobox: BentoBox
    private set

class PrestigeAddon : Addon(), CoroutineScope by MainScope() {

    val logger by lazy { KotlinLogging.logger("WPrestiges") }
    internal val session = UUID.randomUUID().toString()

    // Command stuff
    lateinit var audiences: BukkitAudiences
    lateinit var commandManager: PaperCommandManager<CommandSender>
    lateinit var commandAnnotation: AnnotationParser<CommandSender>
    lateinit var commandHelp: MinecraftHelp<CommandSender>

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
        addon = this
        bentobox = BentoBox.getInstance()

        kguava = CacheBuilder.newBuilder()
            .expireAfterWrite(Long.MAX_VALUE, TimeUnit.DAYS)
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
        if (dataFolder.exists() == false)
            dataFolder.mkdirs().also { logger.info { "[IO] dataFolder ~'${dataFolder.path}' created" } }

        // Command CommandFramework
        val executionCoordinatorFunction =
            AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().build()
        try {
            commandManager = PaperCommandManager(
                plugin,
                executionCoordinatorFunction,
                ::identity,
                ::identity
            )
        } catch (exception: Exception) {
            logger.error { "Failed to initialize CommandFramework::CommandManager" }
        }
        finally {
            audiences = BukkitAudiences.create(plugin)
            commandHelp = MinecraftHelp("/prestige help", audiences::sender, commandManager)
            if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER))
                commandManager.registerBrigadier()
            if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
                commandManager.registerAsynchronousCompletions()

            val commandMetaFunction: java.util.function.Function<ParserParameters, CommandMeta> =
                java.util.function.Function<ParserParameters, CommandMeta> { parser ->
                    CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, parser.get(StandardParameters.DESCRIPTION, "No description"))
                        .build()
                }
            commandAnnotation = AnnotationParser(
                commandManager,
                CommandSender::class.java,
                commandMetaFunction
            )
            commandManager.buildAndRegister(
                "wpdebug"
            ) {
                senderType = CommandSender::class
                permission = "wp.admin.debug"
                handler {
                    it.sender.sendMessage("[@] DEBUG")
                    it.sender.sendMessage(commandManager.commands.joinToString(", "))
                }
            }
            commandAnnotation.parse(this)
            logger.info { "Successfully installed CommandFramework Cloud 1.3" }
        }
        super.onLoad()
    }
    override fun onEnable() {

        oneblock = addon.plugin.addonsManager.getAddonByName<AOneBlock>("AOneBlock").get()

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



    }

    override fun onDisable() {  }

    @CommandMethod("prestige")
    @CommandDescription("Basic seto of commands for WPrestige")
    fun command(
        @NotNull sender: CommandSender
    ) {
        addon.commandHelp.queryCommands("", sender)
    }

    @CommandMethod("prestige claim")
    @CommandDescription("Claims island reward for prestige level")
    @CommandPermission("wp.claim")
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
                        sender.sendMessage("[WPrestige] No rewards for ya'").also { return }
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.2")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set cmi.command.warp.pvp")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7/warp pvp")
                    return
                }
                number >= 3  -> {
                    if (sender.hasPermission("prestige.3.new"))
                        sender.sendMessage("[WPrestige] No rewards for ya'").also { return }
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.3.new")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set enchantgui.enchant")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set oregen.p3")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7Magický Enchant§a. §7/warp enchant")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7MAGIC COBBLESTONE GENERATOR T1§a.")
                    return
                }
                number >= 4 || number >= 5 || number >= 6 || number >= 7 || number >= 8 || number >= 9 -> {
                    if (sender.hasPermission("prestige.4"))
                        sender.sendMessage("[WPrestige] No rewards for ya'").also { return }
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.$number")
                    sender.inventory?.addItem(ItemStack(Material.NETHER_STAR, 2))
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aZískal jsi §72x Nether Star")
                    return
                }
                number >= 10 -> {
                    if (sender.hasPermission("prestige.10"))
                        sender.sendMessage("[WPrestige] No rewards for ya'").also { return }
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set prestige.10")
                    addon.server.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} perm set deluxetags.tag.Trihard")
                    sender.sendMessage("§a§l(!) §7§nGRATULUJEME!§f §aPrávě jsi odemkl §7Trihard Tag")
                    return
                }
            }
        }
        sender.sendMessage("[WPrestige] No rewards for ya'")
    }

    @CommandMethod("prestige set <player> <number>", requiredSender = CommandSender::class)
    @CommandDescription("Sets prestige level number if present")
    @CommandPermission("wp.admin.set")
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
    @CommandPermission("wp.top")
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

fun <T> identity(t: T): T = t