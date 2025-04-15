package coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

suspend fun fakeReadFile(fileName: String): String {
    delay(1000)
    return "File contents of $fileName"
}

suspend fun fakeWriteFile(fileName: String, content: String) {
    delay(1000)
    println("Writing to $fileName: $content")
}

fun main(args: Array<String>) = runBlocking {

    if (args.size < 3) {
        println("Usage: <program> <file1> <file2> <output>")
        exitProcess(1)
    }

    val data1 = fakeReadFile(args[0])

    val data2 = fakeReadFile(args[1])

    fakeWriteFile(fileName = args[2], content = "$data1 /n $data2")
}