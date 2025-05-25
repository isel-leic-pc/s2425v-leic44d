package coroutines.flows

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ColdFlows101Demo")

fun main() {
    runBlocking {
        launch {
            logger.info("Coroutine 1 started")
            val flow1 = flow {
                repeat(5) {
                    val value = it + 10
                    logger.info("Emitting $value")
                    emit(value)
                }
            }

            // What happens if we do not call collect()?
            logger.info("Calling flow1.collect()")
            flow1.collect { value ->
                logger.info("Received $value")
                delay(2000)
            }
        }

        launch {
            val flow2 = flow {
                repeat(5) {
                    val value = it + 20
                    logger.info("Emitting $value")
                    emit(value)
                }
            }

            flow2.collect { value ->
                logger.info("Received $value")
                delay(2000)
            }

        }
    }

    logger.info("Done!")
}