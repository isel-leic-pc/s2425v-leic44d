package isel.leic.pc.demos.jmm

var x = 0
var y = 0

var a = 0
var b = 0

/**
 * This program demonstrates the potential for reordering of operations in a multithreaded environment.
 * What are the possible values of x and y after the threads have completed?
 */
fun main() {
    val t1 = Thread.ofPlatform().start {
        a = 1;
        x = b
    }

    val t2 = Thread.ofPlatform().start { b = 1; y = a }
    t1.join()
    t2.join()
    println("x: $x, y: $y")
}