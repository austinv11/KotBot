@file:JvmName("StaticLoggerBinder")

package org.slf4j.impl

import kotbot.logging.LoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

public class StaticLoggerBinder : LoggerFactoryBinder {
    
    companion object {
        const val REQUESTED_API_VERSION: String = "1.6.99"
        
        @JvmStatic
        fun getSingleton() : StaticLoggerBinder = StaticLoggerBinder()
    }
    
    override fun getLoggerFactory() = LoggerFactory()

    override fun getLoggerFactoryClassStr() = LoggerFactory::class.java.name;
}
