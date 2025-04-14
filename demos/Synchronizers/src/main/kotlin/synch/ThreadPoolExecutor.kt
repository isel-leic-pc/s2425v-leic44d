package synch

import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A simple thread pool executor that manages a fixed number of threads.
 * @param threadPoolSize the number of threads in the pool
 */
class ThreadPoolExecutor(private val threadPoolSize: Int = 1) : Executor {

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private val threads: List<Thread>
    private val workQueue = mutableListOf<Runnable>()
    private var isShuttingDown = false

    private fun startWorker(): Thread =
        Thread.ofPlatform().start {
            while (true) {
                var work: Runnable? = null
                guard.withLock {

                    work = workQueue.removeFirstOrNull()

                    if (work == null) {
                        if(isShuttingDown) {
                            return@start
                        }
                        condition.await()
                    }
                }
                work?.run()
            }
        }

    init {
        threads = List(threadPoolSize) {
            startWorker()
        }
    }

    /**
     * Schedules the specified task for execution in the thread pool.
     * @param [task] the command to be executed
     * @throws RejectedExecutionException if the executor is shutting down
     */
    override fun execute(task: Runnable) {
        guard.withLock {
            if (isShuttingDown) {
                throw RejectedExecutionException("ThreadPoolExecutor is shutting down")
            }
            workQueue.add(task)
            condition.signal()
        }
    }

    /**
     * Initiates the shutdown of the thread pool, preventing new tasks from being submitted.
     * Executing tasks will continue to run until completion.
     */
    fun shutdown() {
        guard.withLock {
            isShuttingDown = true
            condition.signalAll()
        }
    }


    /**
     * Waits for the shutdown process to complete, blocking until all tasks have
     * completed execution after a shutdown request.
     */
    fun awaitTermination(): Unit {
        threads.forEach { it.join() }
    }
}


fun main() {
    val executor = ThreadPoolExecutor(2)

    repeat(10) { i ->
        executor.execute {
            println("Task $i is running")
            Thread.sleep(1000)
        }
    }

    executor.shutdown()
    executor.awaitTermination()
}