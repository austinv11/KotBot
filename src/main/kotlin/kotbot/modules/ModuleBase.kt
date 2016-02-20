package kotbot.modules

import kotbot.KotBot
import kotbot.wrapper.bufferedRequest
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.EventSubscriber
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.modules.IModule
import java.util.*

/**
 * This provides a few extensions to IModule in order to make things like command handling easier.
 */
abstract class ModuleBase : IModule {
    
    private val commands = LinkedList<ICommand>()
    protected var client: IDiscordClient? = null

    /**
     * An array of commands which would be automatically managed by this module.
     */
    abstract val moduleCommands: Array<ICommand>
    
    /**
     * Registers a command into this module.
     */
    final fun registerCommand(command: ICommand) {
        commands.add(command)
    }

    /**
     * Unregisters a command into this module.
     */
    final fun unregisterCommand(command: ICommand) {
        val commandToUnregister = commands.find { it.name == command.name } //Done to prevent issues with instances
        commands.remove(commandToUnregister)
    }
    
    @EventSubscriber
    final fun onMessage(event: MessageReceivedEvent) {
        if (!KotBot.CONFIG.blackListedChannels.contains(event.message.channel.id)) {
            if (event.message.content.startsWith(KotBot.CONFIG.commandPrefix)) {
                val command = (if (event.message.content.contains(" ")) event.message.content.split(" ")[0] else event.message.content).substring(1)
                val foundCommand : ICommand? = commands.find { commandObj -> commandObj.name == command || commandObj.aliases.contains(command) }
                
                if (foundCommand != null) {
                    val split = event.message.content.split(" ")
                    val params = if (split.size > 1) split.subList(1, split.size) else listOf<String>()
                    if (!foundCommand(client!!, params, event.message))
                        bufferedRequest { event.message.channel.sendMessage("Error executing command, correct usage for command ${foundCommand.name} is:\n${foundCommand.formatCommandSyntaxMessage()}") }
                }
            }
        }
    }

    override fun enable(client: IDiscordClient?): Boolean {
        this.client = client
        moduleCommands.forEach { registerCommand(it) }
        return true
    }

    override fun disable() {
        client = null
        moduleCommands.forEach { unregisterCommand(it) }
    }
    
    override fun getVersion() = KotBot.VERSION
    
    override fun getMinimumDiscord4JVersion() = KotBot.DISCORD4J_VERSION

    override fun getAuthor() = KotBot.AUTHOR
}
