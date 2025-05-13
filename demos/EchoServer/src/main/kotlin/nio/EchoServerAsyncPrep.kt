package isel.leic.pc.demos.nio

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel

private val logger: Logger = LoggerFactory.getLogger("Async ES")
private const val PORT = 9090

fun main() {
    logger.info("Starting echo server on port $PORT")
    runEchoServer()
    logger.info("Echo server stopped")
}

private fun runEchoServer() {
    val serverSocket = AsynchronousServerSocketChannel
        .open()
        .bind(InetSocketAddress("0.0.0.0", PORT))

    serverSocket.use {
        logger.info("Waiting for clients to connect...")
        while (true) {
            val clientSocketChannel = serverSocket.accept().get()
            Thread.ofPlatform().start {
                handleClient(clientSocketChannel)
            }
        }
    }
}

private fun handleClient(clientSocketChannel: AsynchronousSocketChannel): Unit {
    logger.info("Client connected: ${clientSocketChannel.remoteAddress}")
    clientSocketChannel.use {
        val buffer = ByteBuffer.allocate(1024)

        // Read the message from the client
        it.read(buffer).get()
        buffer.flip()
        val message = Charsets.UTF_8.decode(buffer).toString()

        logger.info("Received message: ${message.trimEnd()}")

        // Echo the message back to the client
        buffer.clear()
        buffer.put("Echo: $message".toByteArray(Charsets.UTF_8))
        buffer.flip()
        it.write(buffer).get()

        logger.info("Echoed message back to client")
    }
}

