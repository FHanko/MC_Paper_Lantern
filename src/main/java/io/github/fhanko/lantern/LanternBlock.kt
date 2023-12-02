package io.github.fhanko.lantern

import com.jeff_media.customblockdata.events.CustomBlockDataMoveEvent
import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent
import io.github.fhanko.kplugin.blocks.AnimatedBlock
import io.github.fhanko.kplugin.blocks.handler.MoveHandler
import io.github.fhanko.kplugin.items.ItemData
import io.github.fhanko.kplugin.items.handler.ClickHandler
import io.github.fhanko.kplugin.util.mm
import net.kyori.adventure.text.Component
import net.minecraft.util.Mth
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Light
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

open class LanternBlock(textures: MutableList<String>, private val lightStrengths: List<Int>, id: Int, name: Component, lore: List<Component>)
    : AnimatedBlock(textures,id, Material.ACACIA_PLANKS, name, lore), ClickHandler, MoveHandler, ConfigurationSerializable {
    override val opaque = true
    companion object {
        private val faces = listOf(BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH)
        val lightData = ItemData(PersistentDataType.INTEGER, "lightData")
        fun placeLight(location: Location, lightPower: Int) {
            val block = location.world.getBlockAt(location)
            lightData.setBlock(block, lightPower)
            if (lightPower == 0) {
                block.blockData = Bukkit.createBlockData(Material.AIR)
                return
            }

            val data = Bukkit.createBlockData(Material.LIGHT)
            (data as Light).apply { level = lightPower }
            block.blockData = data
        }
    }

    override fun rightClickBlock(e: PlayerInteractEvent) {
        if (!e.player.hasPermission("lantern.use")) return
        if (e.isBlockInHand) return
        val block = e.clickedBlock!!
        nextFrame(block)

        updateLight(block, lightStrengths.getOrElse(getFrame(block) ?: 0) { _ -> 0 })
    }

    private fun updateLight(block: Block, lightPower: Int) {
        faces.forEach {
            val rel = block.getRelative(it)

            if (rel.type == Material.AIR || rel.type == Material.LIGHT) placeLight(rel.location, lightPower)
            else lightData.setBlock(rel, lightPower)
        }
    }

    private fun removeLight(block: Block) {
        faces.forEach {
            val rel = block.getRelative(it)

            lightData.removeBlock(rel)
            if (rel.type == Material.LIGHT) rel.blockData = Bukkit.createBlockData(Material.AIR)
        }
    }

    override fun broke(e: BlockBreakEvent) {
        super.broke(e)
        lightData.removeBlock(e.block)
    }

    override fun place(e: BlockPlaceEvent) {
        super.place(e)
        updateLight(e.block, lightStrengths.getOrElse(0) { _ -> 0 })
    }

    override fun destroy(e: CustomBlockDataRemoveEvent) {
        super.destroy(e)
        removeLight(e.block)
    }

    override fun move(e: CustomBlockDataMoveEvent) {
        val be = e.bukkitEvent
        if (be is BlockPistonExtendEvent) be.isCancelled = true
        if (be is BlockPistonRetractEvent) be.isCancelled = true
        e.isCancelled = true
    }

    @Suppress("UNCHECKED_CAST")
    constructor(map: MutableMap<String, Any>) : this(
        map["textures"] as MutableList<String>,
        (map["light"] as List<Int>).map { Mth.clamp(it, 0, 15) },
        (map["id"] as Int),
        mm.deserialize(map["name"] as String),
        (map["lore"] as List<String>).map { mm.deserialize(it) }
    )

    override fun serialize(): MutableMap<String, Any> { return HashMap() }
}