package kotbot.modules.base.commands

import kotbot.KotBot
import kotbot.modules.ICommand
import kotbot.wrapper.bufferedRequest
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

class RestartCommand : ICommand {

    override val name = "restart"
    override val aliases = arrayOf("reload", "r")
    override val helpMessage = "Restarts the bot (only the owner can use this command)."
    override val parameterNames = emptyArray<String>()

    override fun invoke(client: IDiscordClient, params: List<String>, message: IMessage, author: IUser, channel: IChannel): Boolean {
        if (author.id == KotBot.CONFIG.ownerID) {
            KotBot.WRAPPER?.reload()
        } else {
            bufferedRequest { channel.sendMessage("Cannot restart bot, you are not the owner!") }
        }
        return true
    }
}
