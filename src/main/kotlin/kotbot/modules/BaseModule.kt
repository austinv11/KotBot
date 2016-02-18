package kotbot.modules

import kotbot.KotBot
import kotbot.wrapper.bufferedRequest
import sx.blah.discord.handle.EventSubscriber
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.modules.IModule
import sx.blah.discord.util.MessageBuilder
import java.util.*

/**
 * This provides a few extensions to IModule in order to make things like command handling easier.
 */
abstract class BaseModule : IModule {
    
    private val commands = LinkedList<ICommand>()

    /**
     * Registers a command into this module
     */
    final fun registerCommand(command: ICommand) {
        commands.add(command)
    }
    
    @EventSubscriber
    final fun onMessage(event: MessageReceivedEvent) {
        if (!KotBot.CONFIG.blackListedChannels.contains(event.message.channel.id)) {
            if (event.message.content.startsWith(KotBot.CONFIG.commandPrefix)) {
                val command = (if (event.message.content.contains(" ")) event.message.content.split(" ")[0] else event.message.content).substring(1)
                val foundCommand : ICommand? = commands.find { commandObj -> commandObj.name == command || commandObj.aliases.contains(command) }
                
                if (foundCommand != null) {
                    if (!foundCommand(event.message.content.split(" "), event.message))
                        bufferedRequest { event.message.channel.sendMessage("Error executing command, correct usage for command ${foundCommand.name} is:\n${formatCommandSyntaxMessage(foundCommand)}") }
                }
            }
        }
    }

    /**
     * Formats a syntax message for the provided command
     */
    protected fun formatCommandSyntaxMessage(command: ICommand): String {
        val joiner = StringJoiner(" ")
        joiner.add(KotBot.CONFIG.commandPrefix+command.name)
        command.parameterNames.forEach { joiner.add("<$it>") }
        return "${MessageBuilder.Styles.INLINE_CODE}${joiner.toString()}${MessageBuilder.Styles.INLINE_CODE}"
    }
}
