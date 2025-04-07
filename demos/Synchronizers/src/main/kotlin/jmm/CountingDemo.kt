package isel.leic.pc.demos.jmm

@Volatile var stopIt = false
var counterValue = 0

fun main() {

    val countingThread = Thread.ofPlatform().start {
        while (!stopIt) {
            counterValue += 1
        }
        println("countingThread ending")
    }

    repeat(times = 3) {
        Thread.sleep(2000)
        println("counterValue = $counterValue")
    }

    println("stopping counting thread")
    stopIt = true

    countingThread.join()
    println("Done!")
}