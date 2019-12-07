import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.test.assertEquals

fun <T> List<T>.toBlockingQueue(capacity: Int): BlockingQueue<T> {
    var queue = ArrayBlockingQueue<T>(capacity)
    queue.addAll(this)
    return queue
}

fun executeAmplifierControllers(program: Program, settings: List<Int>) : Int {
    if (settings.size != 5) throw IllegalStateException("Settings ($settings) must be of length 5!")

    return settings.fold(0, { input, setting ->
        val inputQueue = listOf(setting, input).toBlockingQueue(2)
        val outputQueue = ArrayBlockingQueue<Int>(1)

        Thread() { Computer(inputQueue, outputQueue).execute(program.toMutableList()) }.start()
        outputQueue.take()
    })
}

fun findMaxThrusterSignal(program: Program, settingsRange: IntRange): Pair<Int, List<Int>> {
    return settingsRange.toList()
            .getAllPermutations()
            .map { it -> Pair(executeAmplifierControllers(program, it), it) }
            .maxBy { it.first }
            ?: throw IllegalStateException("No max thruster signal found for the given program")
}

fun testExample1() {
    val program = mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0)
    val expected = Pair(43210, listOf(4,3,2,1,0))
    assertEquals(expected, findMaxThrusterSignal(program, 0..4))
}

fun testExample2() {
    val program = mutableListOf(3,23,3,24,1002,24,10,24,1002,23,-1,23,
            101,5,23,23,1,24,23,23,4,23,99,0,0)
    val expected = Pair(54321, listOf(0,1,2,3,4))
    assertEquals(expected, findMaxThrusterSignal(program, 0..4))
}

fun testExample3() {
    val program = mutableListOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
            1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
    val expected = Pair(65210, listOf(1,0,4,3,2))
    assertEquals(expected, findMaxThrusterSignal(program, 0..4))
}

fun testExample4() {
    val program = mutableListOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
            1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
    assertEquals(65210, executeAmplifierControllers(program, listOf(1,0,4,3,2)))
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
    val (signal, setting) = findMaxThrusterSignal(program, 0..4)
    println("Max thruster signal: $signal (for setting=$setting)")
}

fun main() {
    tests()
    part01()
}