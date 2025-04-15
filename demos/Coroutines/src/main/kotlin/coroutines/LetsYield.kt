package coroutines

import org.slf4j.LoggerFactory
import kotlin.coroutines.*

private val logger = LoggerFactory.getLogger("Lets Yield demo")

private var continuations = mutableListOf<Continuation<Unit>>()
private val noOpCompletion = Continuation<Unit>(EmptyCoroutineContext) {
    logger.info("Continuation completed with result: $it")
}

private suspend fun yield() {
    suspendCoroutine { continuation ->
        continuations.add(continuation)
    }
}

private suspend fun f1() {
    val strings = listOf("Hello", "World")
    strings.forEach {
        logger.info(it)
        yield()
    }
}

private suspend fun f2() {
    val strings = listOf("Olá", "Mundo")
    strings.forEach {
        logger.info(it)
        yield()
    }
}

fun main() {
    ::f1.startCoroutine(noOpCompletion)
    ::f2.startCoroutine(noOpCompletion)

    while (continuations.isNotEmpty()) {
        logger.info("Executing next continuation")
        val firstContinuation = continuations.removeFirst()
        firstContinuation.resume(Unit)
    }
}