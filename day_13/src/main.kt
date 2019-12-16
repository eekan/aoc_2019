import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.test.assertEquals


fun List<Int>.toBlockingQueue() : BlockingQueue<Long> {
    val queue = ArrayBlockingQueue<Long>(size+1)
    forEach { queue.add(it.toLong()) }
    return queue
}

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun part01() {
    var program = loadProgram("day_13/input.txt").toMutableList()

    val screen = mutableMapOf<Pair<Long, Long>, Long>()
    val outputQueue = ArrayBlockingQueue<Long>(8000)
    val inputQueue = ArrayBlockingQueue<Long>(1)
    val computer = Computer(inputQueue, outputQueue, program.toMutableList())

    playGame(computer, screen, inputQueue, outputQueue)
    println("[Part 01] Output: ${screen.values.count { it == 2L }}")
}

fun playGame(computer: Computer,
             screen: MutableMap<Pair<Long, Long>, Long>,
             inputQueue: BlockingQueue<Long>,
             outputQueue: BlockingQueue<Long>): Long {
    var score = -1L
    var ball = Pair(-1L, -1L)
    var paddle = Pair(-1L, -1L)

    var hasInput = false
    while(true) {
        var execRes = computer.execute(hasInput)

        while (outputQueue.peek() != null) {
            val x = outputQueue.take()
            val y = outputQueue.take()
            val id = outputQueue.take()
            if (x == -1L && y == 0L) {
                score = id
                continue
            }

            if (id == 3L) {
                paddle = Pair(x, y)
            } else if (id == 4L) {
                ball = Pair(x, y)
            }

            screen[Pair(x, y)] = id
        }

        if (execRes == 99L) return score

        inputQueue.add(ball.first.compareTo(paddle.first).toLong())
        hasInput = true
    }

}

fun part02() {
    var program = loadProgram("day_13/input.txt").toMutableList()
    program[0] = 2L

    val screen = mutableMapOf<Pair<Long, Long>, Long>()
    val outputQueue = ArrayBlockingQueue<Long>(8000)
    val inputQueue = ArrayBlockingQueue<Long>(1)
    val computer = Computer(inputQueue, outputQueue, program.toMutableList())

    val score = playGame(computer, screen, inputQueue, outputQueue)

    println("[Part 02] Output: $score")
}

fun main() {
    part01()
    part02()
}