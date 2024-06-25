package io.github.karmasmp.karma.player.visuals

import io.github.karmasmp.karma.player.KarmaPlayer
import io.github.karmasmp.karma.player.PlayerManager.getPlayer
import io.github.karmasmp.karma.plugin
import io.github.karmasmp.karma.util.Sounds

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title

import org.bukkit.Bukkit
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
    fun gainLife(karmaPlayer: KarmaPlayer) {
        val bukkitPlayer = karmaPlayer.uuid.getPlayer().player
        if(bukkitPlayer != null) {
            bukkitPlayer.playEffect(EntityEffect.TOTEM_RESURRECT)
            bukkitPlayer.showTitle(
                Title.title(
                    Component.text("You gained a life!", NamedTextColor.GREEN),
                    Component.text(""),
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
                bukkitPlayer.location,
                50,
                1.0,
                1.0,
                1.0
            )
            bukkitPlayer.world.spawnParticle(
                Particle.END_ROD,
                bukkitPlayer.location,
                50,
                2.0,
                2.0,
                2.0,
            )
            if(karmaPlayer.lives == 0) {
                bukkitPlayer.showTitle(
                    Title.title(
                        Component.text("You are now a Ghost!", NamedTextColor.GRAY),
                        Component.text(""),
                        Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(3),
                            Duration.ofMillis(750)
                        )
                    )
                )
                Sounds.playProgressSoundLoop(bukkitPlayer, "entity.ghast.scream", false) // Could make global for more effect?
                playGhostAnimation(bukkitPlayer)
            } else {
                bukkitPlayer.showTitle(
                    Title.title(
                        Component.text("You lost a life!", NamedTextColor.RED),
                        Component.text(""),
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
            val ghostHeadAmount = 5
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
        armourStand.equipment.helmet = getGhostHead()
        armourStand.addScoreboardTag("ghost.armour_stand.${player.uniqueId}")

        return Pair(bat, armourStand)
    }

    private fun getGhostHead() : ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD, 1)
        val headMeta = head.itemMeta as SkullMeta
        headMeta.owningPlayer = Bukkit.getOfflinePlayer("Horror") // Set to a specific profile due to texture editing requiring NMS, which I hate.
        head.itemMeta = headMeta
        return head
    }
}