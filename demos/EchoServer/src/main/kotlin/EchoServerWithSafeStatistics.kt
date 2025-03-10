package org.example

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

private val logger: Logger = LoggerFactory.getLogger("ES with safe statistics")
private const val PORT = 9090

fun main() {
    logger.info("Starting echo server on port $PORT")
    runEchoServer()
    logger.info("Echo server stopped")
}

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

private fun handleClient(clientSocket: Socket) {
    logger.info("Client connected: ${clientSocket.remoteSocketAddress}")
    clientSocket.use {
        // TODO: Start session
        it.getInputStream().bufferedReader().use { reader ->
            it.getOutputStream().bufferedWriter().use { writer ->
                writer.writeLine("Hello! Please type something and press Enter:")
                while (true) {
                    val line = reader.readLine()
                    // TODO: Increment session's message count
                    if (line.trim().lowercase() == "exit") {
                        writer.writeLine("Bye!")
                        break
                    }
                    // TODO: Handle stats command, that returns:
                    //  Total clients handled, active session count and message count per active client
                    writer.writeLine("You wrote: $line")
                }
            }
        }
        // TODO: End session
    }
}
