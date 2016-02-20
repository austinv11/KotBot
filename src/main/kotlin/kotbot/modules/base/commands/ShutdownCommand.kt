package kotbot.modules.base.commands

import kotbot.KotBot
import kotbot.modules.ICommand
import kotbot.wrapper.bufferedRequest
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

class ShutdownCommand : ICommand {

    override val name = "shutdown"
    override val aliases = arrayOf("kill", "close")
    override val helpMessage = "Shuts down the bot (only the owner can use this command)."
    override val parameterNames = emptyArray<String>()

    override fun invoke(client: IDiscordClient, params: List<String>, message: IMessage, author: IUser, channel: IChannel): Boolean {
        if (author.id == KotBot.CONFIG.ownerID) {
            KotBot.WRAPPER?.close()
        } else {
            bufferedRequest { channel.sendMessage("Cannot shutdown bot, you are not the owner!") }
        }
        return true
    }
}
