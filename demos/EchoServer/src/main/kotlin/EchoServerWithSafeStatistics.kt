package org.example

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

private val logger: Logger = LoggerFactory.getLogger("ES with safe statistics")
private const val PORT = 9090

private val serverInfo = SafeButNonScalableServerInfo()

fun main() {
    logger.info("Starting echo server on port $PORT")
    runEchoServer()
    logger.info("Echo server stopped")
}

/**
 * The echo server loop.
 */
private fun runEchoServer() {
    ServerSocket().use { serverSocket ->
        serverSocket.bind(InetSocketAddress("0.0.0.0", PORT))
        while (true) {
            logger.info("Waiting for clients to connect...")
            val clientSocket = serverSocket.accept()
            Thread.ofPlatform().start {
                handleClient(clientSocket)
            }
        }
    }
}

private const val EXIT = "exit"
private const val STATS = "stats"

/**
 * Handles the client connection.
 * @param [clientSocket] the client socket
 */
private fun handleClient(clientSocket: Socket) {
    logger.info("Client connected: ${clientSocket.remoteSocketAddress}")
    serverInfo.withSession(clientSocket) { session ->
        clientSocket.getInputStream().bufferedReader().use { reader ->
            clientSocket.getOutputStream().bufferedWriter().use { writer ->
                writer.writeLine("Hello ${clientSocket.remoteSocketAddress}! Please type something and press Enter:")
                while (true) {
                    val line = reader.readLine()
                    serverInfo.incrementMessageCount(session)
                    if (line.trim().lowercase() == EXIT) {
                        writer.writeLine("Bye!")
                        break
                    }
                    if (line.trim().lowercase() == STATS) {
                        writer.writeLine("Session stats: ${serverInfo.getStats()}")
                        break
                    }
                    writer.writeLine("You wrote: $line")
                }
            }
        }
    }
}
