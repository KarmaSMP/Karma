package io.github.karmasmp.karma.player.nametag

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.chat.Formatting
import io.github.karmasmp.karma.player.admin.Admin
import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.*
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
                if(Admin.getAdmins().contains(player.uniqueId) && !Admin.getStaffMode().contains(player.uniqueId)) {
                    nametag.customName(Component.text(Formatting.Prefix.ADMIN_PREFIX.value).append(Component.text(player.name, NamedTextColor.DARK_RED)))
                } else if(Admin.getAdmins().contains(player.uniqueId) && Admin.getStaffMode().contains(player.uniqueId)) {
                    nametag.customName(Component.text(Formatting.Prefix.ADMIN_PREFIX.value).append(Component.text(player.name, NamedTextColor.DARK_RED)).append(Component.text(" [STAFF MODE]", NamedTextColor.GOLD)))
                } else {
                    nametag.customName(ChatUtils.livesAsComponent(player.getKarmaLives()).append(Component.space()).append(Component.text(player.name, NamedTextColor.WHITE)))
                }
                /** Below check is to be removed when updating to 1.21, as passengers and vehicles can be transported across dimensions in this version. **/
                if(player.location.block.type == Material.NETHER_PORTAL || player.location.block.type == Material.END_PORTAL || player.location.block.type == Material.END_GATEWAY || player.eyeLocation.block.type == Material.NETHER_PORTAL || player.eyeLocation.block.type == Material.END_PORTAL || player.eyeLocation.block.type == Material.END_GATEWAY || (player.isAdmin() && !player.isInStaffMode())) {
                    player.removePassenger(nametag)
                    nametag.teleport(Location(player.world, player.x, player.y + 1.5, player.z))
                } else {
                    if(!player.passengers.contains(nametag)) {
                        player.addPassenger(nametag)
                    }
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