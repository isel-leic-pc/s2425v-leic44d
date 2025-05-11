package isel.leic.pc.demos.blocking

import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Represents the session information.
 * @property [remoteSocketAddress] the remote socket address
 * @property [messageCount] the number of messages received
 */
data class SafeButNotScalableSessionInfo(
    val remoteSocketAddress: SocketAddress,
    val messageCount: Int = 0
)

/**
 * Represents the server statistics.
 * @property [totalClients] the total number of clients
 * @property [sessions] the list of sessions
 * @property [activeSessions] the number of active sessions
 */
data class SafeButNotScalableStats(
    val totalClients: Int = 0,
    val sessions: List<SafeButNotScalableSessionInfo> = emptyList(),
    val activeSessions: Int = sessions.size
)

/**
 * Represents the server information. This implementation is safe but not scalable because
 * it uses a single lock to protect all shared state.
 */
class SafeButNonScalableServerInfo {

    private val sessions = mutableMapOf<SocketAddress, SafeButNotScalableSessionInfo>()
    private var totalClients = 0
    private val theLock = ReentrantLock()

    /**
     * Creates a new session for the client with [remoteSocketAddress]
     * @param [remoteSocketAddress] the clients' remote address information
     * @return the newly created [SessionInfo] instance
     */
    private fun createSession(remoteSocketAddress: SocketAddress): SafeButNotScalableSessionInfo {
        theLock.withLock {
            val session = SafeButNotScalableSessionInfo(remoteSocketAddress)
            sessions[remoteSocketAddress] = session
            totalClients += 1
            return session
        }
    }

    /**
     * Ends the given session.
     * @param [sessionInfo] the session to be terminated
     */
    fun endSession(sessionInfo: SafeButNotScalableSessionInfo) {
        theLock.withLock {
            sessions.remove(sessionInfo.remoteSocketAddress)
        }
    }

    /**
     * Increments the number of received messages for the given session.
     * @param [sessionInfo] the session to increment the message count for
     */
    fun incrementMessageCount(sessionInfo: SafeButNotScalableSessionInfo) {
        theLock.withLock {
            val session = checkNotNull(sessions[sessionInfo.remoteSocketAddress])
            sessions[sessionInfo.remoteSocketAddress] = session.copy(messageCount = sessionInfo.messageCount + 1)
        }
    }

    /**
     * Returns the server statistics.
     * @return the server statistics
     */
    fun getStats() =
        theLock.withLock {
            SafeButNotScalableStats(totalClients = totalClients, sessions = sessions.values.toList()).toString()
        }

    /**
     * Executes the given [block] within the context of a session.
     * @param [clientSocket] the client socket
     * @param [block] the block of code to execute
     */
    fun withSession(clientSocket: Socket, block: (SafeButNotScalableSessionInfo) -> Unit) {
        clientSocket.use { socket ->
            lateinit var session: SafeButNotScalableSessionInfo
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

