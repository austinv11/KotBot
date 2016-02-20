package kotbot.modules.base.commands

import kotbot.modules.ICommand
import kotbot.modules.ModuleBase
import kotbot.modules.formatCommandSyntaxMessage
import kotbot.wrapper.bufferedRequest
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.modules.IModule
import sx.blah.discord.util.MessageBuilder
import java.util.*

class HelpCommand : ICommand {
    
    override val name = "help"
    override val aliases = arrayOf("h", "?")
    override val helpMessage = "Lists and provides information about all commands."
    override val parameterNames = arrayOf("optional: commandName or module:[module name]")

    override fun invoke(client: IDiscordClient, params: List<String>, message: IMessage, author: IUser, channel: IChannel): Boolean {
        when (params.size) {
            0 -> {
                sendCommandList(client, channel)
            }
            1 -> {
                val param = params[0]
                if (param.contains("module:")) {
                    val module = client.moduleLoader.loadedModules.find { it.name.equals(param.replace("module:", "").replace("_", " "), true) }
                    if (module == null) {
                        bufferedRequest { channel.sendMessage("Module `$param` could not be found") }
                    } else {
                        sendCommandList(client, channel, module)
                    }
                } else {
                    val command = findCommands(client).find { it.name == param || it.aliases.contains(param) }
                    if (command == null) {
                        bufferedRequest { channel.sendMessage("Command `$param` could not be found.") }
                    } else {
                        val joiner = StringJoiner("\n")
                        val commaJoiner = StringJoiner(",")
                        joiner.add("Help page for `$param`")
                        command.aliases.forEach { commaJoiner.add(it) }
                        joiner.add("Aliases: `${commaJoiner.toString()}`")
                        joiner.add("Help message: `${command.helpMessage}`")
                        joiner.add("Syntax: ${command.formatCommandSyntaxMessage()}")
                        bufferedRequest { channel.sendMessage(joiner.toString()) }
                    }
                }
            }
            else -> return false
        }
        return true
    }
    
    private fun findCommands(client: IDiscordClient, module: IModule? = null): MutableList<ICommand> {
        val commandsList: MutableList<ICommand> = LinkedList()
        val moduleList: List<IModule>
        if (module != null)
            moduleList = listOf(module)
        else 
            moduleList = client.moduleLoader.loadedModules.filter { it is ModuleBase }
        moduleList.forEach { commandsList.addAll((it as ModuleBase).moduleCommands) }
        return commandsList
    }
    
    private fun sendCommandList(client: IDiscordClient, channel: IChannel, module: IModule? = null) {
        val commandsList = findCommands(client, module)
        val joiner = StringJoiner("\n")
        commandsList.forEach { joiner.add("* ${it.name}") }
        bufferedRequest { MessageBuilder(client).withChannel(channel)
                .withContent("There are ${commandsList.size} commands${if (module != null) " for module `${module.name}`" else ""}:\n")
                .appendContent(joiner.toString(), MessageBuilder.Styles.CODE)
                .send() }
    }
}
