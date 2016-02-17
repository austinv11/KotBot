package kotbot

import kotbot.wrapper.Discord4JWrapper
import org.slf4j.LoggerFactory

/**
 * A wrapper for Discord4J which allows for continuous uptime even through reloads.
 */
public fun main(args: Array<String>) {
    if (args.size < 2)
        throw IllegalArgumentException("Expected at least two arguments (email, password)")
    
    KotBot.WRAPPER = Discord4JWrapper(args[0], args[1])
}

public class KotBot {
    
    companion object {
        val LOGGER = LoggerFactory.getLogger("KotBot")
        
        var WRAPPER: Discord4JWrapper? = null
    }
}
