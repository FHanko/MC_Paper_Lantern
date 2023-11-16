package io.github.fhanko.lantern

import io.github.fhanko.kplugin.blocks.BlockBase
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.type.Light
import org.bukkit.persistence.PersistentDataType

object Util {
    fun placeLight(location: Location, lightPower: Int) {
        val data = Bukkit.createBlockData(Material.LIGHT)
        (data as Light).apply { level = lightPower }
        val block = location.world.getBlockAt(location)
        block.blockData = data
        BlockBase.markBlock(block, LIGHT_KEY, PersistentDataType.INTEGER, lightPower)
    }
}