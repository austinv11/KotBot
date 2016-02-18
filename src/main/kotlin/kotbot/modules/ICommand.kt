package kotbot.modules

import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

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
    open operator fun invoke(params: List<String>, message: IMessage, author: IUser = message.author): Boolean
}
