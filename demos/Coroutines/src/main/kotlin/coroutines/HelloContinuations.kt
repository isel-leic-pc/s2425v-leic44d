package coroutines

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.coroutines.*

private val logger = LoggerFactory.getLogger("Hello continuations demo")

private var theContinuation: Continuation<Unit>? = null
private val noOpCompletion = Continuation<Unit>(EmptyCoroutineContext) {
    logger.info("Continuation completed with result: $it")
}

suspend fun f() {
    logger.info("Inside f(), before suspendCoroutine")
    suspendCoroutine { cont ->
        logger.info("Inside f(), inside suspendCoroutine")
        theContinuation = cont
    }
    logger.info("Inside f(), after suspendCoroutine")
}

fun main() = runBlocking {
    logger.info("main() starts")
    ::f.startCoroutine(noOpCompletion)
    theContinuation?.resume(Unit)
    logger.info("main() ends")
}