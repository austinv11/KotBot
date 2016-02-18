package kotbot.wrapper

import kotbot.KotBot
import sx.blah.discord.util.HTTP429Exception
import java.util.*

private val TIMER: Timer = Timer("Buffered Request Timer")

/**
 * This buffers a function, queueing it for another attempt if it hits an HTTP429Exception 
 */
fun <T> bufferedRequest(action: () -> T?): FutureValue<T> {
    val value: FutureValue<T> = FutureValue { action() }
    try {
        value.run()
    } catch (e: HTTP429Exception) {
        KotBot.LOGGER.debug("Queueing request for ${e.retryDelay}ms")
        TIMER.schedule(object: TimerTask() {
            override fun run() {
                bufferedRequest { value.run() }
            }
        }, e.retryDelay)
    }
    return value
}

public class FutureValue<T>(val action: () -> T?) {

    /**
     * This is the value of the action provided, or null if it wasn't executed
     */
    var returnVal: T? = null
    /**
     * This is true when the action was executed
     */
    var didExecute: Boolean = false

    /**
     * Attempts to run the action, if it returns true then it successfully ran
     */
    fun run(): Boolean {
        if (!didExecute) {
            try {
                returnVal = action()
                didExecute = true
            } catch (e: Exception) {
                didExecute = false
                throw e
            }
        }
        return didExecute
    }
}
