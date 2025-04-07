package isel.leic.pc.demos.jmm

import java.util.concurrent.TimeUnit

/**
 * This is a naive implementation of a producer-consumer problem using a busy-wait loop.
 * It demonstrates the potential issues with memory visibility and thread synchronization.
 * The producer thread sets the value of `result` to 42, while the consumer thread spins
 * in a loop until `done` is set to true.
 *
 * What are the possible results of this program?
 */

@Volatile var done = false
var result = 0
var spinCount = 0

fun main() {

    println("Starting consumer")
    val consumer = Thread.ofPlatform().daemon().start {
        while (!done) { spinCount++ }
        println("Consumer stopped spinning. Result is $result")
    }

    // Let's give the consumer some time to start spinning
    TimeUnit.SECONDS.sleep(1)
    println("Starting producer")
    val producer = Thread.ofPlatform().daemon().start {
        result = 42
        done = true
    }

    println("Waiting for threads to finish")
    producer.join(2000)
    consumer.join(2000)

    println("Producer isAlive=${producer.isAlive} ; consumer.isAlive=${consumer.isAlive}")
    println("Done! Result: $result")
}