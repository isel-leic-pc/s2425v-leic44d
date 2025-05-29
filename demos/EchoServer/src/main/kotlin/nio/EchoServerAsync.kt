package isel.leic.pc.demos.nio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val logger: Logger = LoggerFactory.getLogger("Async ES")
private const val PORT = 9090

fun main() {
    logger.info("Starting echo server on port $PORT")
    runEchoServer()
    logger.info("Echo server stopped")
}

private fun runEchoServer() = runBlocking(Dispatchers.IO) {
    val serverSocket = AsynchronousServerSocketChannel
        .open()
        .bind(InetSocketAddress("0.0.0.0", PORT))

    serverSocket.use {
        logger.info("Waiting for clients to connect...")
        while (true) {
            val clientSocketChannel = serverSocket.suspendAccept()
            launch { handleClient(clientSocketChannel) }
        }
    }
}

private suspend fun handleClient(clientSocketChannel: AsynchronousSocketChannel) {
    clientSocketChannel.use {
        logger.info("Client connected: ${clientSocketChannel.remoteAddress}")
        val buffer = ByteBuffer.allocate(1024)

        // Read the message from the client
        clientSocketChannel.suspendRead(buffer)
        buffer.flip()
        val message = Charsets.UTF_8.decode(buffer).toString()
        logger.info("Received message: ${message.trimEnd()}")

        // Echo the message back to the client
        buffer.clear()
        buffer.put("Echo: $message".toByteArray(Charsets.UTF_8))
        buffer.flip()
        clientSocketChannel.suspendWrite(buffer)
    }
}

private suspend fun AsynchronousSocketChannel.suspendRead(buffer: ByteBuffer): Int =
    suspendCoroutine { cont ->
        read(buffer, null, object : CompletionHandler<Int, Void?> {
            override fun completed(result: Int, attachment: Void?) {
                cont.resume(result)
            }

            override fun failed(exc: Throwable?, attachment: Void?) {
                cont.resumeWithException(exc ?: Exception("Read failed"))
            }
        })
    }

private suspend fun AsynchronousSocketChannel.suspendWrite(buffer: ByteBuffer): Int =
    suspendCoroutine { cont ->
        write(buffer, null, object : CompletionHandler<Int, Void?> {
            override fun completed(result: Int, attachment: Void?) {
                cont.resume(result)
            }

            override fun failed(exc: Throwable?, attachment: Void?) {
                cont.resumeWithException(exc ?: Exception("Write failed"))
            }
        })
    }

private suspend fun AsynchronousServerSocketChannel.suspendAccept(): AsynchronousSocketChannel =
    suspendCancellableCoroutine { cont ->
        accept(cont, object : CompletionHandler<AsynchronousSocketChannel, Any?> {
            override fun completed(result: AsynchronousSocketChannel, attachment: Any?) {
                cont.resume(result)
            }

            override fun failed(exc: Throwable?, attachment: Any?) {
                cont.resumeWithException(exc ?: Exception("Accept failed"))
            }
        })

        cont.invokeOnCancellation {
            try {
                close()
            } catch (e: Exception) {
                logger.error("Failed to close server socket channel", e)
            }
        }
    }
