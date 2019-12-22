import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

fun reverse(direction: Long) = when (direction) {
    1L -> 2L
    2L -> 1L
    3L -> 4L
    else -> 3L
}

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun doWalk(c: Computer, inputQueue: BlockingQueue<Long>, outputQueue: BlockingQueue<Long>, direction: Long): Long {
    inputQueue.put(direction)
    c.execute(true)

    return outputQueue.take()
}

fun Pair<Long, Long>.move(direction: Long): Pair<Long, Long> {
    return when (direction) {
        1L -> Pair(first, second + 1)
        2L -> Pair(first, second - 1)
        3L -> Pair(first - 1, second)
        else -> Pair(first + 1, second)
    }
}

fun walk(c: Computer, spaceMap: MutableMap<Pair<Long, Long>, Long>, inputQueue: BlockingQueue<Long>,
         outputQueue: BlockingQueue<Long>, pos: Pair<Long, Long>, direction: Long): Long {
    val nextPos = pos.move (direction)

    if (spaceMap.containsKey(nextPos)) { // already visited
        return Long.MIN_VALUE
    }

    val out = doWalk(c, inputQueue, outputQueue, direction)
    spaceMap[nextPos] = out

    val res = when(out) {
        0L -> Long.MIN_VALUE // Hit the wall
        1L -> 1 + (1..4L).map { walk(c, spaceMap, inputQueue, outputQueue, nextPos, it) }.max()!!
        else -> {
            // Walk rest of map, then return distance 1
            (1..4L).map { walk(c, spaceMap, inputQueue, outputQueue, nextPos, it) }
            1L
        }
    }

    if (out != 0L) {
        // Go back
        doWalk(c, inputQueue, outputQueue, reverse(direction))
    }
    return res
}

fun explore(c: Computer, spaceMap: MutableMap<Pair<Long, Long>, Long>, inputQueue: BlockingQueue<Long>,
            outputQueue: BlockingQueue<Long>, pos: Pair<Long, Long>): Long {
    return (1..4L).map { walk(c, spaceMap, inputQueue, outputQueue, pos, it) }.max() ?: throw IllegalStateException()
}

fun part01() {
    var program = loadProgram("day_15/input.txt").toMutableList()

    val spaceMap = mutableMapOf<Pair<Long, Long>, Long>()
    val outputQueue = ArrayBlockingQueue<Long>(1)
    val inputQueue = ArrayBlockingQueue<Long>(1)
    val computer = Computer(inputQueue, outputQueue, program.toMutableList())

    val res = explore(computer, spaceMap, inputQueue, outputQueue, Pair(0L, 0L))
    printSpaceMap(spaceMap)

    println("[Part 01] Output: $res")
}

fun Pair<Long, Long>.getAdjacently(): Set<Pair<Long, Long>> {
    return (1..4L).map { move(it) }.toSet()
}

fun fillAdjacent(spaceMap: MutableMap<Pair<Long, Long>, Long>, positions: Set<Pair<Long, Long>>): Set<Pair<Long, Long>> {
    var empty = spaceMap.filterValues { it == 1L }.keys.toSet()

    var filled = positions.flatMap {
        //println("it=$it, adj=${it.getAdjacently()}")
        it.getAdjacently().intersect(empty)
    }.toSet()

    filled.forEach { spaceMap[it] = 3L }
    return filled
}

fun part02() {
    var program = loadProgram("day_15/input.txt").toMutableList()

    val spaceMap = mutableMapOf<Pair<Long, Long>, Long>()
    val outputQueue = ArrayBlockingQueue<Long>(1)
    val inputQueue = ArrayBlockingQueue<Long>(1)
    val computer = Computer(inputQueue, outputQueue, program.toMutableList())

    // Build spaceMap
    explore(computer, spaceMap, inputQueue, outputQueue, Pair(0L, 0L))

    // Find location of oxygen system
    var pos = setOf(spaceMap.filterValues { it == 2L }.toList().first().first)

    var step = 0
    while(true) {
        pos = fillAdjacent(spaceMap, pos)
        if (pos.isEmpty())
            break

        step++
        //printSpaceMap(spaceMap.toMutableMap())
    }

    println("[Part 02] Output: $step")
}

fun main() {
    part01()
    part02()
}

fun printSpaceMap(spaceMap: MutableMap<Pair<Long, Long>, Long>) {
    val xs = spaceMap.keys.map { it.first }
    val ys = spaceMap.keys.map { it.second }
    val xMin = xs.min() ?: throw IllegalStateException()
    val xMax = xs.max() ?: throw IllegalStateException()
    val yMin = ys.min() ?: throw IllegalStateException()
    val yMax = ys.max() ?: throw IllegalStateException()

    val width = xMax - xMin + 1
    val height = yMax - yMin + 1
    val screen = MutableList(height.toInt()) { MutableList(width.toInt()) { " " } }

    spaceMap.keys.forEach {
        screen[(it.second - yMin).toInt()][(it.first - xMin).toInt()] =
                when (spaceMap[it]) {
                    0L -> "\u2585"
                    1L -> " "
                    2L -> "\u26F3"
                    3L -> "0"
                    else -> "?"
                }
    }
    screen[(0L - xMin).toInt()][(0L - yMin).toInt()] = "\u26EC"
    println(screen.joinToString(separator = "\n") { it.joinToString(separator = " ") })

}
