package sequences

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StructuredConcurrencyDemo")

fun main() {
    val aSequence = sequence {
        var counter = 0
        while (true) {
            logger.info("yielding $counter")
            yield(counter++)
        }
    }

    aSequence.forEach {
        logger.info(it.toString())
        Thread.sleep(1000)
    }
}