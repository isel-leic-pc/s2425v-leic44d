package isel.leic.pc.demos.blocking

import isel.leic.pc.demos.writeLine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

private val logger: Logger = LoggerFactory.getLogger("ES with unsafe counter")
private const val PORT = 9090

fun main() {
    logger.info("Starting echo server on port $PORT")
    runEchoServer()
    logger.info("Echo server stopped")
}

private fun runEchoServer() {
    ServerSocket().use { serverSocket ->
        serverSocket.bind(InetSocketAddress("0.0.0.0", PORT))
        var clientCount = 0
        while (true) {
            logger.info("Waiting for clients to connect...")
            val clientSocket = serverSocket.accept()
            Thread.ofPlatform().start {
                clientCount += 1 // This is wrong! Read-modify-write is not atomic
                handleClient(clientSocket, clientCount)
            }
        }
    }
}

private fun handleClient(clientSocket: Socket, clientCount: Int) {
    logger.info("Client connected: ${clientSocket.remoteSocketAddress}")
    clientSocket.use {
        it.getInputStream().bufferedReader().use { reader ->
            it.getOutputStream().bufferedWriter().use { writer ->
                logger.info("Handling client #$clientCount")
                writer
                    .writeLine("Hello, client #$clientCount! Please type something and press Enter:")
                    .writeLine("You wrote: ${reader.readLine()}")
                    .writeLine("Bye!")
            }
        }
    }
}
