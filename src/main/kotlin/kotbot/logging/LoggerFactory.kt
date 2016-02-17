package kotbot.logging

import org.slf4j.ILoggerFactory

/**
 * Simple class to get Logger instances
 */
class LoggerFactory : ILoggerFactory {
    
    override fun getLogger(p0: String?) = LoggerAdapter(p0?: "Logger")
}
