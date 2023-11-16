package io.github.fhanko.lantern

import io.github.fhanko.kplugin.items.ItemBase
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LanternCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (args?.size !in 1..2) return false
        if (args?.getOrNull(0)?.toIntOrNull() == null) return false
        val id = args[0].toInt()
        val amount = args.getOrNull(1)?.toIntOrNull()
        ItemBase.give(sender, id, amount ?: 1)
        return true
    }
}