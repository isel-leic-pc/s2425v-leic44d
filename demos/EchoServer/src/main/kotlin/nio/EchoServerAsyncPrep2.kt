package isel.leic.pc.demos.nio

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

private val logger: Logger = LoggerFactory.getLogger("Async ES")
private const val PORT = 9090

fun main() {
    logger.info("Starting echo server on port $PORT")
    readln()
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
            handleClient(clientSocketChannel)
        }
    }
}

private fun handleClient(clientSocketChannel: AsynchronousSocketChannel) {
    logger.info("Client connected: ${clientSocketChannel.remoteAddress}")
    val buffer = ByteBuffer.allocate(1024)

    fun handleClientInternal() {
        clientSocketChannel.read(buffer, null, object : CompletionHandler<Int, Void?> {
            override fun completed(result: Int, attachment: Void?) {
                if (result == -1) {
                    logger.info("Client disconnected: ${clientSocketChannel.remoteAddress}")
                    clientSocketChannel.close()
                    return
                }

                buffer.flip()
                val message = Charsets.UTF_8.decode(buffer).toString().trimEnd()
                logger.info("Received message: $message")

                // Echo the message back to the client
                buffer.clear()
                buffer.put("Echo: $message".toByteArray(Charsets.UTF_8))
                buffer.flip()

                clientSocketChannel.write(buffer, null, object : CompletionHandler<Int, Void?> {
                    override fun completed(result: Int, attachment: Void?) {
                        buffer.clear()
                        clientSocketChannel.close()
                    }

                    override fun failed(exc: Throwable, attachment: Void?) {
                        logger.error("Failed to write to client", exc)
                        clientSocketChannel.close()
                    }
                })
            }

            override fun failed(exc: Throwable, attachment: Void?) {
                logger.error("Failed to read from client", exc)
                clientSocketChannel.close()
            }
        })
    }

    handleClientInternal() // Start reading from the client
}
