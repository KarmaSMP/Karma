package io.github.karmasmp.karma.player.nametag

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.chat.Formatting
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import io.github.karmasmp.karma.player.creator.Creator.isLive
import io.github.karmasmp.karma.plugin

import net.kyori.adventure.text.Component

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team

import java.util.*

object PlayerNametag {
    private val playerNametags = mutableMapOf<UUID, AreaEffectCloud>()
    private val nametagTasks = mutableMapOf<UUID, BukkitRunnable>()

    private val invisibleTags = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("invisibleTags-${UUID.randomUUID()}")

    fun buildNametag(player: Player) {
        invisibleTags.addPlayer(player)

        val nametag : AreaEffectCloud = player.world.spawn(player.location, AreaEffectCloud::class.java)
        nametag.duration = Int.MAX_VALUE
        nametag.radius = 0F
        nametag.waitTime = 0
        nametag.color = Color.BLACK
        nametag.customName(Component.text(""))
        nametag.isCustomNameVisible = true

        playerNametags[player.uniqueId] = nametag
        player.addPassenger(nametag)

        runNametagTask(player, player.uniqueId, nametag)
    }

    fun destroyNametag(uuid: UUID) {
        playerNametags[uuid]?.remove()
        playerNametags.remove(uuid)
    }

    fun getNametag(player: Player): AreaEffectCloud? {
        return playerNametags[player.uniqueId]
    }

    private fun runNametagTask(player: Player, uuid: UUID, nametag : AreaEffectCloud) {
        val nametagTask = object : BukkitRunnable() {
            override fun run() {
                if(player.isAdmin() && !player.isInStaffMode()) {
                    if(player.isLive()) {
                        nametag.customName(Formatting.allTags.deserialize("<prefix:admin> <dark_red>${player.name}"))
                    } else {
                        nametag.customName(Formatting.allTags.deserialize("<prefix:admin> <dark_red>${player.name}<reset> <prefix:creator>"))
                    }
                } else if(player.isAdmin() && player.isInStaffMode()) {
                    if(player.isLive()) {
                        nametag.customName(Formatting.allTags.deserialize("<prefix:admin> <dark_red>${player.name}<reset> <prefix:staff> <prefix:creator>"))
                    } else {
                        nametag.customName(Formatting.allTags.deserialize("<prefix:admin> <dark_red>${player.name}<reset> <prefix:staff>"))
                    }
                } else {
                    if(player.isLive()) {
                        nametag.customName(ChatUtils.livesAsComponent(player.getKarmaLives()).append(Formatting.allTags.deserialize(" <white>${player.name} <prefix:creator>")))
                    } else {
                        nametag.customName(ChatUtils.livesAsComponent(player.getKarmaLives()).append(Formatting.allTags.deserialize(" <white>${player.name}")))
                    }
                }
                if(!player.passengers.contains(nametag)) {
                    player.addPassenger(nametag)
                }
                if(player.world != nametag.world) {
                    nametag.teleport(player)
                }
                if(player.isSneaking) {
                    nametag.isSneaking = true
                } else {
                    nametag.isSneaking = false
                }
                if(player.isInvisible || player.gameMode == GameMode.SPECTATOR || player.isDead || (player.isAdmin() && !player.isInStaffMode())) {
                    nametag.isCustomNameVisible = false
                } else {
                    nametag.isCustomNameVisible = true
                }
                if(!player.isOnline) {
                    cancelNametagTask(uuid)
                }
            }
        }
        nametagTask.runTaskTimer(plugin, 0L, 1L)
        nametagTasks[player.uniqueId] = nametagTask
    }

    fun cancelNametagTask(uuid: UUID) {
        destroyNametag(uuid)
        nametagTasks[uuid]?.cancel()
        nametagTasks.remove(uuid)
    }

    fun setup() {
        invisibleTags.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
    }

    fun destroy() {
        invisibleTags.unregister()
        for(world in Bukkit.getWorlds()) {
            for(aec in world.getEntitiesByClass(AreaEffectCloud::class.java)) {
                aec.remove()
            }
        }
    }
}