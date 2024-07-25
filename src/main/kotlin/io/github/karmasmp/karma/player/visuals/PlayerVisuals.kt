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
import io.github.karmasmp.karma.player.creator.Creator.isLive
import io.github.karmasmp.karma.player.nametag.PlayerNametag
import io.github.karmasmp.karma.plugin
import io.github.karmasmp.karma.util.Pathfinder
import io.github.karmasmp.karma.util.Sounds

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title

import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

import java.time.Duration

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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
                        when(player.getKarmaLives()) {
                            3 -> {
                                player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:three>"))
                            }
                            2 -> {
                                player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:two>"))
                            }
                            1 -> {
                                player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:one>"))
                            }
                            0 -> {
                                player.sendActionBar(Formatting.allTags.deserialize("${if(player.inventory.itemInOffHand.type == Material.AIR) LIFE_IMAGE_SPACER else LIFE_IMAGE_OFFHAND_SPACER}<actionbar:ghost>"))
                            } else -> {
                                player.sendActionBar(Formatting.allTags.deserialize("<red>Unable to parse current state to action bar."))
                            }
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

    fun death(karmaPlayer: KarmaPlayer, deathMessage: Component) {
        val bukkitPlayer = karmaPlayer.uuid.getPlayer().player
        val plainDeathMessage = PlainTextComponentSerializer.plainText().serialize(deathMessage)

        if(bukkitPlayer != null) {
            parseDeathMessage(bukkitPlayer, plainDeathMessage)
            Pathfinder.clearNearbyTargets(bukkitPlayer)

            val playerInventory = bukkitPlayer.inventory.contents.filterNotNull()
            spreadItemsXp(bukkitPlayer, playerInventory, bukkitPlayer.location)

            bukkitPlayer.clearActivePotionEffects()

            val deathVehicle : AreaEffectCloud = bukkitPlayer.world.spawn(bukkitPlayer.location, AreaEffectCloud::class.java)
            deathVehicle.duration = Int.MAX_VALUE
            deathVehicle.radius = 0F
            deathVehicle.waitTime = 0
            deathVehicle.color = Color.BLACK
            deathVehicle.addScoreboardTag("${bukkitPlayer.uniqueId}-death-vehicle")
            deathVehicle.addPassenger(bukkitPlayer)

            hidePlayer(bukkitPlayer)

            deathEffects(bukkitPlayer)

            /** Scheduled Respawn and Post Respawn **/
            object : BukkitRunnable() {
                override fun run() {
                    respawn(bukkitPlayer)
                    object : BukkitRunnable() {
                        override fun run() {
                            postRespawn(bukkitPlayer, deathVehicle)
                        }
                    }.runTaskLater(plugin, 20L)
                }
            }.runTaskLater(plugin, 160L)

            if(karmaPlayer.lives == 0) {
                bukkitPlayer.showTitle(
                    Title.title(
                        Formatting.allTags.deserialize("<gray>You are now a Ghost!"),
                        Formatting.allTags.deserialize("<dark_gray>${plainDeathMessage}"),
                        Title.Times.times(
                            Duration.ofMillis(250),
                            Duration.ofSeconds(8),
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
                        Formatting.allTags.deserialize("<gray>${plainDeathMessage}"),
                        Title.Times.times(
                            Duration.ofMillis(250),
                            Duration.ofSeconds(8),
                            Duration.ofMillis(750)
                        )
                    )
                )
            }
        }
    }

    private fun parseDeathMessage(player: Player, plainDeathMessage: String) {
        val parsedDeathMessage = plainDeathMessage.replace(player.name,
            "${if(player.getKarmaLives() + 1 >= 3) "<green>"
            else if(player.getKarmaLives() + 1 == 2) "<yellow>"
            else if(player.getKarmaLives() + 1 == 1) "<red>"
            else "<dark_gray>"}${player.name}<reset>"
        )
        ChatUtils.messageAudience(Audience.audience(Bukkit.getOnlinePlayers()), "<red><prefix:skull><reset> $parsedDeathMessage.", false)
    }

    fun hidePlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.hidePlayer(plugin, player)
        }
    }

    fun showPlayer(player: Player) {
        for(other in Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player)
        }
    }

    private fun deathEffects(player: Player) {
        player.world.strikeLightningEffect(player.location)
        player.world.spawnParticle(
            Particle.FLASH,
            player.eyeLocation,
            5,
            0.0,
            0.0,
            0.0
        )
        player.world.spawnParticle(
            Particle.END_ROD,
            player.eyeLocation,
            50,
            2.0,
            2.0,
            2.0
        )
        player.playSound(Sounds.DEATH)
    }

    private fun respawn(player: Player) {
        Pathfinder.clearNearbyTargets(player)
        player.showTitle(
            Title.title(
                Formatting.allTags.deserialize("\uF000"), //TODO: MOVE SCREEN EFFECTS SOMEWHERE ELSE
                Formatting.allTags.deserialize(""),
                Title.Times.times(
                    Duration.ofMillis(250),
                    Duration.ofSeconds(2),
                    Duration.ofMillis(500)
                )
            )
        )
        player.playSound(Sounds.RESPAWN)
    }

    private fun postRespawn(player: Player, deathVehicle: AreaEffectCloud) {
        player.inventory.helmet = ItemStack(Material.AIR, 1)
        player.eject()
        deathVehicle.remove()
        player.teleport(if(player.respawnLocation != null) player.respawnLocation!! else Bukkit.getWorlds()[0].spawnLocation)
        player.fireTicks = 0
        player.health = 20.0
        player.foodLevel = 20
        player.saturation = 5.0f
        Pathfinder.clearNearbyTargets(player)
        showPlayer(player)
        PlayerNametag.buildNametag(player)
    }

    fun disconnectInterruptDeath(player: Player) {
        if(player.vehicle is AreaEffectCloud) {
            player.inventory.helmet = ItemStack(Material.AIR, 1)
            player.vehicle?.remove()
            showPlayer(player)
            player.teleport(if(player.respawnLocation != null) player.respawnLocation!! else Bukkit.getWorlds()[0].spawnLocation)
        }
    }

    fun getExpAtLevel(level: Int): Int {
        return if (level <= 16) {
            (level * level) + 6 * level
        } else if (level <= 31) {
            (2.5 * (level * level) - 40.5 * level + 360.0).toInt()
        } else {
            (4.5 * (level * level) - 162.5 * level + 2220.0).toInt()
        }
    }


    private fun spreadItemsXp(player: Player, playerInventory: List<ItemStack>, deathLoc: Location) {
        val expAmount = getExpAtLevel(player.level) * 0.05
        val expOrb = player.world.spawn(player.location, ExperienceOrb::class.java)
        expOrb.experience = expAmount.roundToInt()

        player.level = 0
        player.exp = 0.0f

        object : BukkitRunnable() {
            override fun run() {
                val angleIncrement = (2 * Math.PI) / playerInventory.size
                var currentAngle = 0.0

                for(itemStack in playerInventory) {
                    val x = deathLoc.x + 0.1 * cos(currentAngle)
                    val z = deathLoc.z + 0.1 * sin(currentAngle)
                    val itemLocation = Location(deathLoc.world, x, deathLoc.y, z)
                    val item = deathLoc.world.dropItem(deathLoc, itemStack)
                    item.velocity = Vector(0, 0, 0)
                    droppedItemParticles(item)
                    item.pickupDelay = 8 * 20
                    item.setGravity(false)
                    item.teleport(itemLocation)

                    val direction = itemLocation.toVector().subtract(deathLoc.toVector()).normalize().multiply(0.05)
                    direction.y = 0.05
                    item.velocity = direction

                    currentAngle += angleIncrement
                }

                player.inventory.clear()
                player.inventory.helmet = ItemStack(Material.CARVED_PUMPKIN, 1)
            }
        }.runTask(plugin)
    }

    private fun droppedItemParticles(item: Item) {
        object : BukkitRunnable() {
            override fun run() {
                if(!item.isDead) {
                    item.world.spawnParticle(
                        Particle.END_ROD,
                        item.location,
                        0,
                        0.0,
                        0.0,
                        0.0
                    )
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 5L)
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
        val bat = player.eyeLocation.world.spawnEntity(player.location, EntityType.BAT) as Bat
        bat.isAwake = true
        bat.isSilent = true
        bat.isInvisible = true
        bat.isInvulnerable = true
        bat.addScoreboardTag("ghost.bat.${player.uniqueId}")

        val armourStand = player.eyeLocation.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand
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
        Sounds.playProgressSoundLoop(player, "block.amethyst_block.resonate", !player.isLive(), isGlobal = false)
        player.world.spawnParticle(Particle.WITCH, player.eyeLocation, 50, 1.0, 1.0, 1.0)
        ChatUtils.messageAudience(Audience.audience(player), "<notifcolour><prefix:warning><reset> You toggled ${if(player.isLive()) { "<green>into" } else { "<red>out<reset> of" }} <notifcolour>Creator<reset> mode.", false)
        ChatUtils.broadcastAdmin("<notifcolour>${player.name} <white>toggled ${if(player.isLive()) { "<green>into" } else { "<red>out<reset> of" }} <notifcolour>Creator <white>mode.", false)
    }
}