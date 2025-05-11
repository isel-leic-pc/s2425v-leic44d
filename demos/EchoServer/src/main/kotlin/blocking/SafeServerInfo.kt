package isel.leic.pc.demos.blocking

import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents the session information.
 * @property [remoteSocketAddress] the remote socket address
 * @property [messageCount] the number of messages received
 */
class SafeSessionInfo(val remoteSocketAddress: SocketAddress) {
    private val _messageCount = AtomicInteger(0)

    val messageCount: Int
        get() = _messageCount.get()

    fun incrementMessageCount() {
        _messageCount.incrementAndGet()
    }
}

/**
 * Represents the server statistics.
 * @property [totalClients] the total number of clients
 * @property [sessions] the list of sessions
 * @property [activeSessions] the number of active sessions
 */
data class SafeStats(
    val totalClients: Int = 0,
    val sessions: List<SafeSessionInfo> = emptyList(),
    val activeSessions: Int = sessions.size
)

/**
 * Represents the server information. This implementation is safe but not scalable because
 * it uses a single lock to protect all shared state.
 */
class SafeServerInfo {

    private val sessions = ConcurrentHashMap<SocketAddress, SafeSessionInfo>()
    private val totalClients = AtomicInteger(0)

    /**
     * Creates a new session for the client with [remoteSocketAddress]
     * @param [remoteSocketAddress] the clients' remote address information
     * @return the newly created [SessionInfo] instance
     */
    private fun createSession(remoteSocketAddress: SocketAddress): SafeSessionInfo {
        val session = SafeSessionInfo(remoteSocketAddress)
        sessions[remoteSocketAddress] = session
        totalClients.incrementAndGet()
        return session
    }

    /**
     * Ends the given session.
     * @param [sessionInfo] the session to be terminated
     */
    fun endSession(sessionInfo: SafeSessionInfo) {
        sessions.remove(sessionInfo.remoteSocketAddress)
    }

    /**
     * Increments the number of received messages for the given session.
     * @param [sessionInfo] the session to increment the message count for
     */
    fun incrementMessageCount(sessionInfo: SafeSessionInfo) {
        val session = checkNotNull(sessions[sessionInfo.remoteSocketAddress])
        session.incrementMessageCount()
    }

    /**
     * Returns the server statistics.
     * @return the server statistics
     */
    fun getStats() =
        SafeStats(
            totalClients = totalClients.get(),
            sessions = sessions.values.toList()
        ).toString()

    /**
     * Executes the given [block] within the context of a session.
     * @param [clientSocket] the client socket
     * @param [block] the block of code to execute
     */
    fun withSession(clientSocket: Socket, block: (SafeSessionInfo) -> Unit) {
        clientSocket.use { socket ->
            lateinit var session: SafeSessionInfo
            try {
                session = createSession(socket.remoteSocketAddress)
                block(session)
            }
            finally {
                endSession(session)
            }
        }
    }
}

