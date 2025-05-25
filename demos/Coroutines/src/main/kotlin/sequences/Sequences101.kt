package sequences

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Sequences101Demo")

fun main() {
    val aSequence = sequence {
        repeat (5) {
            val value = it + 10
            logger.info("yielding $value")
            yield(value)
        }
    }

    val anotherSequence = listOf(1, 2, 3, 4, 5).asSequence()

    aSequence.forEach {
        logger.info(it.toString())
        Thread.sleep(100)
    }

    anotherSequence.forEach {
        logger.info(it.toString())
        Thread.sleep(100)
    }
}