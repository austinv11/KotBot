package kotbot.logging

import org.slf4j.ILoggerFactory

class LoggerFactory : ILoggerFactory {
    
    override fun getLogger(p0: String?) = LoggerAdapter(p0?: "Logger")
}
