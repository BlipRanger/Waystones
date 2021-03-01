package xyz.atrius.waystones.utility

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material.*
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.util.Vector
import xyz.atrius.waystones.configuration
import xyz.atrius.waystones.data.FloodFill
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

val Location.UP: Location
    get() = this.clone().add(0.0, 1.0, 0.0)

val Location.DOWN: Location
    get() = this.clone().add(0.0, -1.0, 0.0)

val Location.NORTH: Location
    get() = this.clone().add(0.0, 0.0, -1.0)

val Location.SOUTH: Location
    get() = this.clone().add(0.0, 0.0, 1.0)

val Location.WEST: Location
    get() = this.clone().add(-1.0, 0.0, 0.0)

val Location.EAST: Location
    get() = this.clone().add(1.0, 0.0, 0.0)

val Location.center: Location
    get() = apply {
        x = floor(x) + 0.5
        z = floor(z) + 0.5
    }

val Location.neighbors: List<Location>
    get() = listOf(UP, DOWN, NORTH, SOUTH, WEST, EAST)

// Returns the code of this location
val Location.locationCode
    get() = "${world?.name}@$blockX:$blockY:$blockZ"

// Determines if the selected block is safe to spawn on
val Location.isSafe: Boolean
    get() = !listOf(UP, UP.UP).map { world?.getBlockAt(it)?.type?.isSolid ?: true }.any { it }

fun Location.range(): Int {
    val config = configuration
    return config.baseDistance() + FloodFill(
        this, config.maxWarpSize(), NETHERITE_BLOCK, EMERALD_BLOCK, DIAMOND_BLOCK, GOLD_BLOCK, IRON_BLOCK
    ).breakdown.entries.sumBy {
        it.value * when(it.key) {
            NETHERITE_BLOCK -> config.netheriteBoost()
            EMERALD_BLOCK   -> config.emeraldBoost()
            DIAMOND_BLOCK   -> config.diamondBoost()
            GOLD_BLOCK      -> config.goldBoost()
            IRON_BLOCK      -> config.ironBoost()
            else            -> 1
        }
    }
}

fun Location.rotateY(angle: Double, amp: Double = 1.0) =
    add(Vector(cos(angle) * amp, 2.0, sin(angle) * amp))

fun Location.sameDimension(other: Location) =
    world == other.world ?: false

fun Location.sameDimension(world: World) =
    world == this.world ?: false

fun Location.playSound(sound: Sound, volume: Float = 1f, pitch: Float = 1f) = Bukkit.getOnlinePlayers()
    .forEach { if (it.world == world) it.playSound(this, sound, volume, pitch)  }