package org.example

import java.io.BufferedWriter

/**
 * Writes to this [BufferedWriter] the line comprised of the text in [line].
 * @return the [BufferedWriter] instance, for fluent use.
 */
fun BufferedWriter.writeLine(line: String): BufferedWriter = also {
    write(line)
    newLine()
    flush()
}