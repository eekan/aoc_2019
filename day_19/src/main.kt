import java.io.File
import java.lang.Integer.max
import java.util.concurrent.ArrayBlockingQueue
import kotlin.system.measureTimeMillis

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun execute(x: Int, y: Int, program: Program): Int {
    val outputQueue = ArrayBlockingQueue<Long>(1)
    val inputQueue = ArrayBlockingQueue<Long>(2)
    inputQueue.put(x.toLong())
    inputQueue.put(y.toLong())
    Computer(inputQueue, outputQueue, program.toMutableList()).execute()
    return outputQueue.take().toInt()
}

operator fun Pair<Int, Int>.plus(that: Pair<Int, Int>): Pair<Int, Int> =
        Pair(this.first + that.first, this.second + that.second)

fun part01() {
    var program = loadProgram("day_19/input.txt").toMutableList()

    val width = 50
    val height = 50
    val result = (0 until width).sumBy { x ->
        (0 until height).count { y -> execute(x, y, program) == 1 }
    }

    println("[Part 01] Output: $result")
}

fun findBestBlockPosition(program: Program, boxWidth: Int, boxHeight: Int): Pair<Int, Int> {
    /*
     * The beam can have "holes" (i.e. completely empty lines), so we need a limit (in boxWidth) for when to
     * stop searching the first beam position in the line. We also use this to limit maximum number of rows to search
     * (in boxHeights)
     */
    var limit = 5
    while(true) {
        limit *= 3
        println("limit= $limit")

        val yRange = 0 .. (limit * boxHeight)
        var x = 0

        for (y in yRange) {
            // x never decrease from row to next row, so start searching from previous first value (ignore empty lines)
            x = (x .. (x + limit * boxWidth)).firstOrNull {
                execute(it, y + (boxHeight - 1), program) == 1
            } ?: continue

            // Checking this diagonal (left bottom -> top right) is enough knowing the direction of the beam is orthogonal
            if (execute(x + boxWidth - 1, y, program) == 1)
                return Pair(x, y)
        }
    }
}

fun part02() {
    var program = loadProgram("day_19/input.txt").toMutableList()

    val targetWidth = 100
    val targetHeight = 100
    var pos = findBestBlockPosition(program, targetWidth, targetHeight)

    println("[Part 02] Pos: $pos")
    println("[Part 02] Output: ${10000 * pos.first + pos.second }")
}


fun main() {
    println("[Part 01] time=${measureTimeMillis { part01() }}")
    println("[Part 02] time=${measureTimeMillis { part02() }}")
}


fun List<List<Int>>.transpose(): List<List<Int>> {
    val res = List(this[0].size) { MutableList(size) { 0} }

    forEachIndexed { x, elements ->
        elements.forEachIndexed { y, value ->
            res[y][x] = value
        }
    }

    return res
}

fun List<List<Int>>.renderGrid(): String {
    return transpose().joinToString("\n") { it.map { e -> when(e) {
        1 -> '#'
        -1 -> 'X'
        else -> '.'
    }}.joinToString("") }
}

fun MutableMap<Pair<Int, Int>, Int>.renderGrid(): String {
    val width = keys.maxBy { it.first }!!.first + 1
    val height = keys.maxBy { it.second }!!.second + 1
    val dim = max(width, height)
    val grid = List(dim) { MutableList(dim) { 0 } }

    forEach{ (pos, value) ->
        grid[pos.first][pos.second] = value
    }

    return grid.renderGrid()
}

