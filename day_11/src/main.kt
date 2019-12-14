import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.ArrayBlockingQueue

private const val BLACK_SYM = ' '
private const val WHITE_SYM = '#'
private const val BLACK = 0L
private const val WHITE = 1L
private const val DEFAULT_COLOR = BLACK
private const val ROT_LEFT = 0L
private const val ROT_RIGHT = 1L

class Robot(private val start: Pair<Long, Long>, private val startColor: Long, private val program: Program) {
    private var colorMap = mutableMapOf<Pair<Long, Long>, Long>()
    private var pos = start
    private var direction = Pair(0L, 1L)

    private fun getColor(p: Pair<Long, Long>): Long {
        return colorMap.getOrPut(p) { DEFAULT_COLOR }
    }

    private fun setColor(p: Pair<Long, Long>, color: Long) {
        colorMap[p] = color
    }

    private operator fun Pair<Long, Long>.plus(that: Pair<Long, Long>): Pair<Long, Long> {
        return Pair(this.first + that.first, this.second + that.second)
    }

    private fun move(rotation: Long) {
        direction = when (rotation) {
            ROT_LEFT -> Pair(-direction.second, direction.first)
            ROT_RIGHT -> Pair(direction.second, -direction.first)
            else -> throw IllegalStateException("Unknown rotation: $rotation")
        }

        pos += direction
    }

    fun start() {
        pos = start
        direction = Pair(0, 1)
        val inputQueue = ArrayBlockingQueue<Long>(1)
        val outputQueue = ArrayBlockingQueue<Long>(2)
        colorMap = mutableMapOf<Pair<Long, Long>, Long>()

        val computerThread = Thread() { Computer(inputQueue, outputQueue).execute(program.toMutableList()) }
        computerThread.start()

        setColor(start, startColor)
        while (true) {
            inputQueue.put(getColor(pos))


            val color = outputQueue.take()
            if (color == END_OF_QUEUE) break
            setColor(pos, color)

            val rotation = outputQueue.take()
            if (rotation == END_OF_QUEUE) break
            move(rotation)
        }

        computerThread.join()
    }

    fun getMap(): MutableMap<Pair<Long, Long>, Long> {
        return colorMap
    }
}

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun part01() {
    var program = loadProgram("day_11/input.txt").toMutableList()

    val robot = Robot(Pair(0,0), BLACK, program)
    robot.start()
    println("[Part 01] Output: ${robot.getMap().size}")
}

fun List<Long>.getRange(): LongRange {
    return (min()!! .. max()!!)
}

fun Map<Pair<Long, Long>, Long>.renderColorMap(): List<List<Char>> {
    val xRange =this.keys.map { it.first }.getRange()
    val yRange = this.keys.map { it.second }.getRange()

    return yRange.reversed().map { y ->
        xRange.map {x ->
            when (getOrDefault(Pair(x, y), BLACK)) {
                BLACK -> BLACK_SYM
                else -> WHITE_SYM
            }
        }
    }
}

fun part02() {
    var program = loadProgram("day_11/input.txt").toMutableList()

    val robot = Robot(Pair(0,0), WHITE, program)
    robot.start()

    val text = robot.getMap().renderColorMap().map { it.joinToString(separator = "") }.joinToString(separator = "\n")
    println("[Part 02] Output:\n$text")
}

fun main() {
    part01()
    part02()
}