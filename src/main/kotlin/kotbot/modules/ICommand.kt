package kotbot.modules

import kotbot.KotBot
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.MessageBuilder
import java.util.*

/**
 * Extension to format a syntax message for the provided command
 */
fun ICommand.formatCommandSyntaxMessage(): String {
    val joiner = StringJoiner(" ")
    joiner.add(KotBot.CONFIG.commandPrefix+this.name)
    this.parameterNames.forEach { joiner.add("<$it>") }
    return "${MessageBuilder.Styles.INLINE_CODE}${joiner.toString()}${MessageBuilder.Styles.INLINE_CODE}"
}

/**
 * This represents a command.
 */
interface ICommand {

    /**
     * This is called to get the command's main name.
     */
    open val name: String

    /**
     * This is called to get all the possible aliases for this command.
     */
    open val aliases: Array<String>

    /**
     * This gets the message displayed from the help message.
     */
    open val helpMessage: String

    /**
     * This gets the names of the command's parameters, used to format the usage message.
     */
    open val parameterNames: Array<String>

    /**
     * Commands are invoked when it is detected in chat. Returns whether the command was successful or not.
     */
    open operator fun invoke(client: IDiscordClient, params: List<String>, message: IMessage, author: IUser = message.author, channel: IChannel = message.channel): Boolean
}
