package io.github.karmasmp.karma.player.visuals

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.chat.Formatting
import io.github.karmasmp.karma.player.KarmaPlayer
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.player.PlayerManager.getPlayer
import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import io.github.karmasmp.karma.player.creator.Creator.isCreator
import io.github.karmasmp.karma.plugin
import io.github.karmasmp.karma.util.Sounds

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.title.Title

import org.bukkit.EntityEffect
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Bat
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

import java.time.Duration

object PlayerVisuals {
    private const val ADMIN_STAFF_MODE_IMAGE_SPACER = "<translate:space.-225>"
    private const val ADMIN_STAFF_MODE_IMAGE_OFFHAND_SPACER = "<translate:space.-285>"
    private const val ADMIN_IMAGE_SPACER = "<translate:space.-205>"
    private const val ADMIN_IMAGE_OFFHAND_SPACER = "<translate:space.-265>"
    private const val LIFE_IMAGE_SPACER = "<translate:space.-200>"
    private const val LIFE_IMAGE_OFFHAND_SPACER = "<translate:space.-260>"

    private const val GHOST_HEAD_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTZjYzM3YjZjNWYxYWE1MWU5YzljOTYwMjNiNDJhM2NiOGI3MDg0N2MwNDE0OGUxMzViYjg4MTk3ZTBiMjRjYyJ9fX0="

    fun actionBar(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                if(player.isOnline) {
                    if(player.isAdmin()) {
                        if(player.isInStaffMode()) {
                            player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) ADMIN_STAFF_MODE_IMAGE_SPACER else ADMIN_STAFF_MODE_IMAGE_OFFHAND_SPACER}<actionbar:admin><actionbar:staffmode>"))
                        } else {
                            player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) ADMIN_IMAGE_SPACER else ADMIN_IMAGE_OFFHAND_SPACER}<actionbar:admin>"))
                        }
                    } else {
                        if(player.getKarmaLives() >= 3) {
                            player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:three>"))
                        } else if(player.getKarmaLives() == 2) {
                            player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:two>"))
                        } else if(player.getKarmaLives() == 1) {
                            player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:one>"))
                        } else {
                            player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:ghost>"))
                        }
                    }
                } else {
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun gainLife(karmaPlayer: KarmaPlayer) {
        val bukkitPlayer = karmaPlayer.uuid.getPlayer().player
        if(bukkitPlayer != null) {
            bukkitPlayer.playEffect(EntityEffect.TOTEM_RESURRECT)
            bukkitPlayer.showTitle(
                Title.title(
                    Formatting.allTags.deserialize("<green>You gained a life!"),
                    Formatting.allTags.deserialize(""),
                    Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(3),
                        Duration.ofMillis(750)
                    )
                )
            )
        }
    }

    fun death(karmaPlayer: KarmaPlayer) {
        val bukkitPlayer = karmaPlayer.uuid.getPlayer().player
        if(bukkitPlayer != null) {
            bukkitPlayer.world.strikeLightningEffect(bukkitPlayer.location)
            bukkitPlayer.world.spawnParticle(
                Particle.EFFECT,
                bukkitPlayer.eyeLocation,
                50,
                1.0,
                1.0,
                1.0
            )
            bukkitPlayer.world.spawnParticle(
                Particle.END_ROD,
                bukkitPlayer.eyeLocation,
                50,
                2.0,
                2.0,
                2.0,
            )
            if(karmaPlayer.lives == 0) {
                bukkitPlayer.showTitle(
                    Title.title(
                        Formatting.allTags.deserialize("<gray>You are now a Ghost!"),
                        Formatting.allTags.deserialize(""),
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(3),
                            Duration.ofMillis(750)
                        )
                    )
                )
                Sounds.playProgressSoundLoop(bukkitPlayer, "entity.ghast.scream", isDescending = false, isGlobal = true)
                playGhostAnimation(bukkitPlayer)
            } else {
                bukkitPlayer.showTitle(
                    Title.title(
                        Formatting.allTags.deserialize("<red>You lost a life!"),
                        Formatting.allTags.deserialize(""),
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(3),
                            Duration.ofMillis(750)
                        )
                    )
                )
            }
        }
    }

    private fun playGhostAnimation(player: Player) {
        object : BukkitRunnable() {
            val ghostHeadAmount = 8
            var timer = 0
            val ghosts = ArrayList<Pair<Bat, ArmorStand>>()
            override fun run() {
                if(timer <= ghostHeadAmount) {
                    val ghost = getGhost(player)
                    ghosts.add(ghost)
                    ghost.first.addPassenger(ghost.second)
                    ghost.first.velocity = Vector(0.0, 0.3, 0.0)
                }
                if(timer >= 5 * 20) {
                    for(ghost in ghosts) {
                        ghost.second.remove()
                        ghost.first.remove()
                    }
                    ghosts.clear()
                    this.cancel()
                } else {
                    timer++
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun getGhost(player: Player) : Pair<Bat, ArmorStand> {
        val bat = player.location.world.spawnEntity(player.location, EntityType.BAT) as Bat
        bat.isAwake = true
        bat.isSilent = true
        bat.isInvisible = true
        bat.isInvulnerable = true
        bat.addScoreboardTag("ghost.bat.${player.uniqueId}")

        val armourStand = player.location.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand
        armourStand.isInvulnerable = true
        armourStand.isInvisible = true
        armourStand.isSmall = true
        armourStand.setGravity(false)
        armourStand.addDisabledSlots(EquipmentSlot.HEAD, EquipmentSlot.BODY, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)
        armourStand.equipment.helmet = getGhostHead(player.playerProfile)
        armourStand.addScoreboardTag("ghost.armour_stand.${player.uniqueId}")

        return Pair(bat, armourStand)
    }

    private fun getGhostHead(playerProfile: PlayerProfile): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD, 1)
        val headMeta = head.itemMeta as SkullMeta
        playerProfile.setProperty(ProfileProperty("textures", GHOST_HEAD_TEXTURE))
        headMeta.playerProfile = playerProfile
        head.itemMeta = headMeta
        return head
    }

    fun toggleCreator(player: Player) {
        Sounds.playProgressSoundLoop(player, "block.amethyst_block.resonate", !player.isCreator(), isGlobal = false)
        player.world.spawnParticle(Particle.WITCH, player.eyeLocation, 50, 1.0, 1.0, 1.0)
        ChatUtils.messageAudience(Audience.audience(player), "<notifcolour><prefix:warning><reset> You toggled ${if(player.isCreator()) { "<green>into" } else { "<red>out<reset> of" }} <notifcolour>Creator<reset> mode.", false)
        ChatUtils.broadcastAdmin("<notifcolour>${player.name} <white>toggled ${if(player.isCreator()) { "<green>into" } else { "<red>out<reset> of" }} <notifcolour>Creator <white>mode.", false)
    }
}