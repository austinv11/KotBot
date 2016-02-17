package kotbot

import kotbot.wrapper.ModuleChangeWatcher
import org.slf4j.LoggerFactory

/**
 * A wrapper for Discord4J which allows for continuous uptime even through reloads.
 */
public fun main(args: Array<String>) {
    val fileWatchThread: Thread = Thread(ModuleChangeWatcher())
    fileWatchThread.start()
}

public class KotBot {
    
   companion object {
        val LOGGER = LoggerFactory.getLogger("KotBot")
        val INSTANCE = KotBot()
        var isReloading: Boolean = false
        
        fun onFileChange() {
            isReloading = true
            INSTANCE.reload()
        }
    }
    
    fun reload() {
        LOGGER.info("Module change detected! Preparing for restart.")
    }
}
