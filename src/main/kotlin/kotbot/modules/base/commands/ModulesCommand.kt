package kotbot.modules.base.commands

import kotbot.modules.ICommand
import kotbot.modules.ModuleBase
import kotbot.modules.base.BaseModule
import kotbot.wrapper.bufferedRequest
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.modules.IModule
import java.util.*

class ModulesCommand : ICommand {
    
    override val name = "modules"
    override val aliases = arrayOf("module", "m", "mods", "mod")
    override val helpMessage = "Provides information and configures modules."
    override val parameterNames = arrayOf("optional: module name enable/disable")

    override fun invoke(client: IDiscordClient, params: List<String>, message: IMessage, author: IUser, channel: IChannel): Boolean {
        when (params.size) {
            0 -> {
                val externalModules: MutableList<IModule> = LinkedList()
                val internalModules: MutableList<IModule> = LinkedList()
                client.moduleLoader.loadedModules.forEach { if (it is ModuleBase) internalModules.add(it) else externalModules.add(it) }
                
                val joiner = StringJoiner("\n")
                if (internalModules.size > 0) {
                    joiner.add("__KotBot Modules:__```")
                    internalModules.forEach { module -> joiner.add("* ${module.name} (enabled: ${BaseModule.enabledModules.find { it.name == module.name } != null})") }
                    joiner.add("```")
                }
                if (externalModules.size > 0) {
                    joiner.add("__External Modules:__```")
                    externalModules.forEach { module -> joiner.add("* ${module.name} v${module.version} by ${module.author} (enabled: ${BaseModule.enabledModules.find { it.name == module.name } != null})") }
                    joiner.add("```")
                }
                
                bufferedRequest { channel.sendMessage(joiner.toString()) }
            }
            2 -> {
                val module = client.moduleLoader.loadedModules.find { it.name.equals(params[0].replace("_", " "), true) }
                if (module == null) {
                    bufferedRequest { channel.sendMessage("Module `${params[0]} not found") }
                } else if (module is BaseModule) {
                    bufferedRequest { channel.sendMessage("Cannot disable the base module!") }
                } else {
                    if (!params[1].equals("enable", true) || !params[1].equals("disable", true)) {
                        return false
                    } else {
                        val enable = params[1].equals("enable", true)
                        if (enable)
                            client.moduleLoader.loadModule(module)
                        else
                            client.moduleLoader.unloadModule(module)
                    }
                }
            }
            else -> return false
        }
        return true
    }
}
