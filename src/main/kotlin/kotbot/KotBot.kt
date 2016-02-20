package kotbot

import com.google.gson.GsonBuilder
import kotbot.configuration.Config
import kotbot.configuration.fromJsonWithComments
import kotbot.configuration.toJsonWithComments
import kotbot.wrapper.Discord4JWrapper
import org.slf4j.LoggerFactory
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.ReadyEvent
import java.io.File
import java.time.LocalDateTime

/**
 * A wrapper for Discord4J which allows for continuous uptime even through reloads.
 */
public fun main(args: Array<String>) {
    Runtime.getRuntime().addShutdownHook(object: Thread() {
        override fun run() {
            KotBot.INSTANCE.client?.logout()
        }
    })
    
    if (KotBot.CONFIG_FILE.exists()) {
        val loadedConfig = KotBot.GSON.fromJsonWithComments<Config>(KotBot.CONFIG_FILE)
        if (loadedConfig != null)
            KotBot.CONFIG = loadedConfig
    }
    
    var email: String? = KotBot.CONFIG.email
    var password: String? = KotBot.CONFIG.password
    
    if ((email == null || password == null) && args.size < 2)
        throw IllegalArgumentException("Expected at least two arguments (email, password)")
    
    try {
        KotBot.WRAPPER = Discord4JWrapper(email?: args[0], password?: args[1])
    } catch(e: RuntimeException) {
        KotBot.LOGGER.error("Unable to start wrapper! Aborting launch...", e)
        return
    }
    
    KotBot.updateConfigFile()
}

public class KotBot {
    
    var client: IDiscordClient? = null
    
    companion object {
        val LOGGER = LoggerFactory.getLogger("KotBot")
        val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val CONFIG_FILE = File("./config.json")
        val INSTANCE = KotBot()
        val VERSION = "1.0.0-SNAPSHOT"
        val DISCORD4J_VERSION = "2.3.0"
        val AUTHOR = "Austin"
        val STARTUP_TIME = LocalDateTime.now()
        
        var WRAPPER: Discord4JWrapper? = null
        var CONFIG: Config = Config()
        var LAST_CLIENT_STARTUP_TIME: LocalDateTime? = null
        
        fun updateConfigFile() {
            KotBot.CONFIG_FILE.writeText(KotBot.GSON.toJsonWithComments(KotBot.CONFIG))
        }
    }
    
    fun onReady(event: ReadyEvent) {
        LAST_CLIENT_STARTUP_TIME = LocalDateTime.now()
        client = event.client
    }
}
