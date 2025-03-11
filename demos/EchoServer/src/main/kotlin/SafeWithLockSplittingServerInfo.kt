package org.example

import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Represents the session information.
 * @property [remoteSocketAddress] the remote socket address
 * @property [messageCount] the number of messages received
 */
class SafeWithLockSplittingSessionInfo(val remoteSocketAddress: SocketAddress) {
    private var _messageCount = 0
    private val sessionLock = ReentrantLock()

    val messageCount: Int
        get() = sessionLock.withLock { _messageCount }

    fun incrementMessageCount() {
        sessionLock.withLock { _messageCount += 1 }
    }
}

/**
 * Represents the server statistics.
 * @property [totalClients] the total number of clients
 * @property [sessions] the list of sessions
 * @property [activeSessions] the number of active sessions
 */
data class SafeWithLockSplittingStats(
    val totalClients: Int = 0,
    val sessions: List<SafeWithLockSplittingSessionInfo> = emptyList(),
    val activeSessions: Int = sessions.size
)

/**
 * Represents the server information. This implementation is safe but not scalable because
 * it uses a single lock to protect all shared state.
 */
class SafeWithLockSplittingServerInfo {

    private val sessions = mutableMapOf<SocketAddress, SafeWithLockSplittingSessionInfo>()
    private var totalClients = 0
    private val theLock = ReentrantLock()

    /**
     * Creates a new session for the client with [remoteSocketAddress]
     * @param [remoteSocketAddress] the clients' remote address information
     * @return the newly created [SessionInfo] instance
     */
    private fun createSession(remoteSocketAddress: SocketAddress): SafeWithLockSplittingSessionInfo {
        theLock.withLock {
            val session = SafeWithLockSplittingSessionInfo(remoteSocketAddress)
            sessions[remoteSocketAddress] = session
            totalClients += 1
            return session
        }
    }

    /**
     * Ends the given session.
     * @param [sessionInfo] the session to be terminated
     */
    fun endSession(sessionInfo: SafeWithLockSplittingSessionInfo) {
        theLock.withLock {
            sessions.remove(sessionInfo.remoteSocketAddress)
        }
    }

    /**
     * Increments the number of received messages for the given session.
     * @param [sessionInfo] the session to increment the message count for
     */
    fun incrementMessageCount(sessionInfo: SafeWithLockSplittingSessionInfo) {
        val session = theLock.withLock {
            checkNotNull(sessions[sessionInfo.remoteSocketAddress])
        }
        session.incrementMessageCount()
    }

    /**
     * Returns the server statistics.
     * @return the server statistics
     */
    fun getStats() =
        theLock.withLock {
            SafeWithLockSplittingStats(totalClients = totalClients, sessions = sessions.values.toList()).toString()
        }

    /**
     * Executes the given [block] within the context of a session.
     * @param [clientSocket] the client socket
     * @param [block] the block of code to execute
     */
    fun withSession(clientSocket: Socket, block: (SafeWithLockSplittingSessionInfo) -> Unit) {
        clientSocket.use { socket ->
            lateinit var session: SafeWithLockSplittingSessionInfo
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

