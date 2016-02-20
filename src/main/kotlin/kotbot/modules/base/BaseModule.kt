package kotbot.modules.base

import kotbot.modules.ICommand
import kotbot.modules.ModuleBase
import kotbot.modules.base.commands.HelpCommand
import kotbot.modules.base.commands.ModulesCommand
import kotbot.modules.base.commands.RestartCommand
import kotbot.modules.base.commands.ShutdownCommand
import sx.blah.discord.handle.EventSubscriber
import sx.blah.discord.handle.impl.events.ModuleDisabledEvent
import sx.blah.discord.handle.impl.events.ModuleEnabledEvent
import sx.blah.discord.modules.IModule

/**
 * This is the base module which is always enabled providing the most basic features of this bot.
 */
class BaseModule : ModuleBase() {
    
    override val moduleCommands: Array<ICommand> = arrayOf(HelpCommand(), ModulesCommand(), RestartCommand(), ShutdownCommand())

    override fun getName() = "Base Module"
    
    @EventSubscriber
    fun onModuleEnabled(event: ModuleEnabledEvent) {
        if (!enabledModules.contains(event.module)) 
            enabledModules.add(event.module)
    }
    
    @EventSubscriber
    fun onModuleDisabled(event: ModuleDisabledEvent) {
        enabledModules.remove(event.module)
    }
    
    companion object {
        val enabledModules = linkedListOf<IModule>()
    }
}
