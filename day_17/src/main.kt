import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

typealias  SpaceMap = Map<Pair<Int, Int>, Long>
fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun BlockingQueue<Long>.getSpaceMap() : SpaceMap {
    val spaceMap = mutableMapOf<Pair<Int, Int>, Long>()
    var x = 0
    var y = 0

    while(peek() != null) {
        val v = take()

        spaceMap[Pair(x, y)] = v
        x++

        if (v == 10L) {
            y++
            x = 0
        }
    }
    return spaceMap
}

fun List<Long>.renderOutput() =
        joinToString(separator = "") { if (it < 128) it.toChar().toString() else it.toString() }


operator fun Pair<Int, Int>.plus(that: Pair<Int, Int>) = Pair(this.first + that.first, this.second + that.second)

fun SpaceMap.getIntersections(): List<Pair<Int, Int>> =
    keys.filter {
        listOf(it, it + Pair(0, 1), it + Pair(1, 0), it + Pair(0, -1), it + Pair(-1, 0))
                .all { p -> get(p) == 35L }
    }

fun SpaceMap.getAlignmentParameters(): List<Int> =
        getIntersections().map { (x, y) -> x * y }

fun generateSpaceMap(program: MutableList<Long>): SpaceMap {
    val outputQueue = LinkedBlockingQueue<Long>()
    val inputQueue = LinkedBlockingQueue<Long>()
    val computer = Computer(inputQueue, outputQueue, program)
    computer.execute()

    return outputQueue.getSpaceMap()
}

fun part01() {
    var program = loadProgram("day_17/input.txt")
    val spaceMap = generateSpaceMap(program.toMutableList())

    println("[Part 01] map: \n${spaceMap.values.toList().renderOutput()}")
    println("[Part 01] Output: ${spaceMap.getAlignmentParameters().sum()}")
}

private fun SpaceMap.findSimplePath(): List<String> {
    fun getDir(v: Long) = when (v.toChar()) {
        '<' -> Pair(-1, 0)
        '>' -> Pair(1, 0)
        '^' -> Pair(0, -1)
        else -> Pair(0, 1)
    }

    fun Map<Pair<Int, Int>, Long>.canMove(p: Pair<Int, Int>, dir: Pair<Int, Int>) = get(p+dir) != null
    fun Pair<Int, Int>.left() = Pair(second, -first)
    fun Pair<Int, Int>.right() = Pair(-second, first)

    var pos = keys.first { get(it) in "<>^v".map (Char::toLong) }
    var dir = getDir(get(pos)!!)
    val path = mutableListOf<String>()
    val scaffolds = filterValues { it == 35L }

    while(true) {
        var steps = 0

        while (scaffolds.canMove(pos, dir)) {
            pos += dir
            steps++
        }

        if (steps > 0) path += steps.toString()

        if (scaffolds.canMove(pos, dir.left())) {
            dir = dir.left()
            path += "L"
        } else if (scaffolds.canMove(pos, dir.right())) {
            dir = dir.right()
            path += "R"
        } else {
            break
        }
    }

    return path
}

fun List<String>.findSubroutines(): List<String> {
    var pattern = "^(.{1,21})\\1*(.{1,21})(?:\\1|\\2)*(.{1,21})(?:\\1|\\2|\\3)*$".toRegex()

    val matchResult = pattern.matchEntire(joinToString(separator = ",", postfix = ","))
            ?: throw IllegalArgumentException("Path do not contain valid subroutines!")

    return matchResult.groups.drop(1).map { it!!.value }.sortedByDescending { it.length }
}

fun Computer.executeWithAsciiInput(input: String) : List<Long> {
    (input + "\n").forEach { addInput(it.toLong()) }
    execute()
    val output = getAllOutput()
    //println(output.renderOutput())
    return output
}

fun part02() {
    var program = loadProgram("day_17/input.txt").toMutableList()
    val spaceMap = generateSpaceMap(program)

    val path = spaceMap.findSimplePath()
    val subroutines = path.findSubroutines()
    var (a, b, c) = subroutines
    var mainRoutine = path.joinToString(separator = ",", postfix = ",")
            .replace(a, "A,")
            .replace(b, "B,")
            .replace(c, "C,")

    println("[Part 02] path: $path")
    println("[Part 02] a: ${a.dropLast(1)}")
    println("[Part 02] b: ${b.dropLast(1)}")
    println("[Part 02] c: ${c.dropLast(1)}")
    println("[Part 02] Main routine: $mainRoutine")

    val outputQueue = LinkedBlockingQueue<Long>()
    val inputQueue = LinkedBlockingQueue<Long>()
    program[0] = 2L
    val computer = Computer(inputQueue, outputQueue, program.toMutableList())
    computer.executeWithAsciiInput(mainRoutine.dropLast(1))
    computer.executeWithAsciiInput(a.dropLast(1))
    computer.executeWithAsciiInput(b.dropLast(1))
    computer.executeWithAsciiInput(c.dropLast(1))
    val out = computer.executeWithAsciiInput("n")

    println("[Part 02] Output: ${out.last()}")
}

fun main() {
     part01()
    part02()
}