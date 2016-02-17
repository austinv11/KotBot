package kotbot

import com.google.gson.GsonBuilder
import kotbot.configuration.Config
import kotbot.configuration.fromJsonWithComments
import kotbot.configuration.toJsonWithComments
import kotbot.wrapper.Discord4JWrapper
import org.slf4j.LoggerFactory
import java.io.File

/**
 * A wrapper for Discord4J which allows for continuous uptime even through reloads.
 */
public fun main(args: Array<String>) {
    if (args.size < 2)
        throw IllegalArgumentException("Expected at least two arguments (email, password)")
    
    if (KotBot.CONFIG_FILE.exists()) {
        val loadedConfig = KotBot.GSON.fromJsonWithComments<Config>(KotBot.CONFIG_FILE)
        if (loadedConfig != null)
            KotBot.CONFIG = loadedConfig
    }
    
    try {
        KotBot.WRAPPER = Discord4JWrapper(args[0], args[1])
    } catch(e: RuntimeException) {
        KotBot.LOGGER.error("Unable to start wrapper! Aborting launch...", e)
        return
    }
    
    KotBot.CONFIG_FILE.writeText(KotBot.GSON.toJsonWithComments(KotBot.CONFIG))
}

public class KotBot {
    
    companion object {
        val LOGGER = LoggerFactory.getLogger("KotBot")
        val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val CONFIG_FILE = File("./config.json")
        var WRAPPER: Discord4JWrapper? = null
        var CONFIG: Config = Config()
    }
}
