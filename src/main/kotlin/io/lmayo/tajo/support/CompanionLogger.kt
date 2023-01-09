package io.lmayo.tajo.support

import org.slf4j.LoggerFactory.getLogger

open class CompanionLogger {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = getLogger(javaClass.enclosingClass)
    }


    fun <T> T.log(block: (T) -> Pair<String, List<Any?>>): T {
        block(this).also { (msg, args) ->
            logger.info(msg, *args.toTypedArray())
        }
        return this
    }

}