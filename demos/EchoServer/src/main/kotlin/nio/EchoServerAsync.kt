package isel.leic.pc.demos.nio

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("Async ES")
private const val PORT = 9090

fun main() {
    logger.info("Starting echo server on port $PORT")
    runEchoServer()
    logger.info("Echo server stopped")
}

private fun runEchoServer() {
    TODO()
}
