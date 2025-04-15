package coroutines

import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

private val logger = LoggerFactory.getLogger("SchedulingDemo")

private val continuations = LinkedBlockingQueue<Continuation<Unit>>()
private val noOpCompletion = object : Continuation<Unit> {
    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        logger.info("Continuation completed with result: $result")
    }
}

private val scheduleExecutor = Executors.newSingleThreadScheduledExecutor()

private suspend fun sleep(durationInMs: Long) {
    suspendCoroutine { continuation ->
        scheduleExecutor.schedule({
            //continuations.put(continuation)
            continuation.resume(Unit)
        }, durationInMs, TimeUnit.MILLISECONDS)
    }
}

private suspend fun f1() {
    val strings = listOf("Hello", "World")
    strings.forEach {
        logger.info(it)
        sleep(2000)
    }
}

private suspend fun f2() {
    val strings = listOf("Olá", "Mundo")
    strings.forEach {
        logger.info(it)
        sleep(2000)
    }
}

private suspend fun f3() {
    val strings = listOf("Bonjour", "Le Monde")
    strings.forEach {
        logger.info(it)
        sleep(2000)
    }
}

fun main() {
    ::f1.startCoroutine(noOpCompletion)
    ::f2.startCoroutine(noOpCompletion)
    ::f3.startCoroutine(noOpCompletion)
    while (true) {
        val nextContinuation = continuations.take()
        nextContinuation.resume(Unit)
    }
}