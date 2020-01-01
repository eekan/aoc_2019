import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import kotlin.system.measureTimeMillis

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}
fun List<Long>.renderOutput() =
        joinToString(separator = "") { if (it < 128) it.toChar().toString() else it.toString() }

fun part01() {
    var program = loadProgram("day_21/input.txt")
    val inputQueue = LinkedBlockingQueue<Long>()
    val outputQueue = LinkedBlockingQueue<Long>()

    val c = Computer(inputQueue, outputQueue, program.toMutableList())

    /**
     * Program to jump iif there is a place to land on, and at least one hole occurs in the three closest tiles:
     *
     * (!A || !B || !C) && D
     */
    inputQueue.putAscii("NOT A T") // T := !A
    inputQueue.putAscii("NOT B J") // J := !B
    inputQueue.putAscii("OR T J")  // J := !A || !B
    inputQueue.putAscii("NOT C T") // T := !C
    inputQueue.putAscii("OR T J")  // T := !A || !B || !C
    inputQueue.putAscii("AND D J") // J := (!A || !B || !C) && D
    inputQueue.putAscii("WALK")

    c.execute()
    println("[Part 01] output: \n${outputQueue.toList().renderOutput()}")
}

fun LinkedBlockingQueue<Long>.putAscii(s: String) {
    (s+"\n").forEach { put(it.toLong()) }
}

fun part02() {
    var program = loadProgram("day_21/input.txt")
    val inputQueue = LinkedBlockingQueue<Long>()
    val outputQueue = LinkedBlockingQueue<Long>()


    val c = Computer(inputQueue, outputQueue, program.toMutableList())

    /**
     *  We need to jump iif any of the three closes tiles are empty, while the fourth is not (so we can land)
     *  and that we from this position either can stay on the ground or do another jump, this yields:
     *
     *  (!A || !B || !C) && D && ((!E && H) || E)  <=> (!A || !B || !C) && D && (H || E)
     */
    inputQueue.putAscii("NOT A T") // T := !A
    inputQueue.putAscii("NOT B J") // J := !B
    inputQueue.putAscii("OR T J")  // J := !A || !B
    inputQueue.putAscii("NOT C T") // T := !C
    inputQueue.putAscii("OR T J")  // T := !A || !B || !C
    inputQueue.putAscii("AND D J") // J := (!A || !B || !C) && D
    inputQueue.putAscii("NOT E T") // T := !E
    inputQueue.putAscii("NOT T T") // T := E
    inputQueue.putAscii("OR H T")  // T := E || H
    inputQueue.putAscii("AND T J") // J := (!A || !B || !C) && D && (E || H)
    inputQueue.putAscii("RUN")

    c.execute()
    println("[Part 02] output: \n${outputQueue.toList().renderOutput()}")
}

fun main() {
    println("[Part 01] time=${measureTimeMillis { part01() }}")
    println("[Part 02] time=${measureTimeMillis { part02() }}")
}
