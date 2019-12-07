import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.test.assertEquals

fun Int.toBlockingQueue(capacity: Int): BlockingQueue<Int> {
    var queue = ArrayBlockingQueue<Int>(capacity)
    queue.add(this)
    return queue
}

fun executeAmpPart1(program: Program, settings: List<Int>) : Int {
    if (settings.size != 5) throw IllegalStateException("Settings ($settings) must be of length 5!")

    return settings.fold(0, { input, setting ->
        val inputQueue = setting.toBlockingQueue(2)
        val outputQueue = ArrayBlockingQueue<Int>(1)
        inputQueue.put(input)

        Thread() { Computer(inputQueue, outputQueue).execute(program.toMutableList()) }.start()
        outputQueue.take()
    })
}

fun executeAmpPart2(program: Program, settings: List<Int>) : Int {
    if (settings.size != 5) throw IllegalStateException("Settings ($settings) must be of length 5!")

    val queues = settings.map { it.toBlockingQueue(2) }.toMutableList()
    queues[0].put(0) // Initial input value for first amplifier
    queues.add(queues[0]) // Use first queue as last output queue

    var computerThreads = settings.indices.map {
        Thread() {
            Computer(queues[it], queues[it + 1]).execute(program.toMutableList())
        }
    }
    computerThreads.forEach { it.start() }

    // Wait for all programs to terminate before getting the final output value
    computerThreads.forEach { it.join() }
    return queues.last().take()
}

fun findMaxThrusterSignal(program: Program, settingsRange: IntRange, runner: (program: Program, settings: List<Int>) -> Int)
        : Pair<Int, List<Int>> {
    return settingsRange.toList()
            .getAllPermutations()
            .map { it -> Pair(runner(program, it), it) }
            .maxBy { it.first }
            ?: throw IllegalStateException("No max thruster signal found for the given program")
}

fun testExample1() {
    val program = mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0)
    val expected = Pair(43210, listOf(4,3,2,1,0))
    assertEquals(expected, findMaxThrusterSignal(program, 0..4, ::executeAmpPart1))
}

fun testExample2() {
    val program = mutableListOf(3,23,3,24,1002,24,10,24,1002,23,-1,23,
            101,5,23,23,1,24,23,23,4,23,99,0,0)
    val expected = Pair(54321, listOf(0,1,2,3,4))
    assertEquals(expected, findMaxThrusterSignal(program, 0..4, ::executeAmpPart1))
}

fun testExample3() {
    val program = mutableListOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
            1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
    val expected = Pair(65210, listOf(1,0,4,3,2))
    assertEquals(expected, findMaxThrusterSignal(program, 0..4, ::executeAmpPart1))
}

fun testExample4() {
    val program = mutableListOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
            1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
    assertEquals(65210, executeAmpPart1(program, listOf(1,0,4,3,2)))
}

fun tests() {
    testExample1()
    testExample2()
    testExample3()
    testExample4()
}

fun loadProgram(fileName : String) : List<Int> {
    return File(fileName).readText().trim().split(",").map { it.toInt() }.toList()
}

fun part01() {
    var program = loadProgram("day_07/input.txt").toMutableList()
    val (signal, setting) = findMaxThrusterSignal(program, 0..4, ::executeAmpPart1)
    println("[Part 01] Max thruster signal: $signal (for setting=$setting)")
}

fun testExample5() {
    val program = mutableListOf(3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,
            27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5)
    assertEquals(139629729, executeAmpPart2(program, listOf(9,8,7,6,5)))
}

fun part02() {
    var program = loadProgram("day_07/input.txt").toMutableList()
    val (signal, setting) = findMaxThrusterSignal(program, 5..9, ::executeAmpPart2)
    println("[Part 02] Max thruster signal: $signal (for setting=$setting)")
}

fun main() {
    tests()
    part01()
    testExample5()
    part02()
}