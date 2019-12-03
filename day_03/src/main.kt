import java.io.File
import java.lang.IllegalStateException
import kotlin.math.abs
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    var steps: Int = 0 // Don't include in equals()/hashCode()...
    constructor(x: Int, y: Int, s: Int): this (x, y) { steps = s}

    override fun compareTo(other: Point): Int {
        return COMPARATOR.compare(this, other)
    }
    companion object {
        private val COMPARATOR =
                Comparator.comparingInt<Point> {it.x}
                        .thenComparingInt { it.y }
    }

    override fun toString(): String {
        return "Point(x=$x, y=$y, steps=$steps)"
    }
}

fun manhattanDistance(p1 : Point, p2 : Point) : Int {
    return abs(p1.x - p2.x) + abs(p1.y - p2.y)
}

fun manhattanDistance(p1 : Point) : Int {
    return manhattanDistance(p1, Point(0,0))
}

fun calculatePath(wire: List<String>): Set<Point> {
    var points : MutableSet<Point> = mutableSetOf()

    var x : Int = 0
    var y : Int = 0
    var steps : Int = 0
    for (step in wire) {
        var distance = step.substring(1).toInt()
        val (dx: Int, dy: Int) = when (step[0]) {
            'R' -> Pair(1, 0)
            'U' -> Pair(0, 1)
            'L' -> Pair(-1, 0)
            'D' -> Pair(0, -1)
            else -> throw IllegalStateException("Unknown direction: ${step[0]}")
        }

        (1..distance).forEach { _ ->
            x += dx
            y += dy
            steps += 1
            // By design, the first Point(x,y) added is the one with fewest steps!
            points.add(Point(x, y, steps))
        }
    }

    return points
}

fun calculatePath(wires : List<List<String>>) : Pair<Set<Point>, Set<Point>> {
    if (wires.size != 2) throw IllegalStateException("Number of wires must be equal to 2!")

    val paths = wires.map { calculatePath(it)}
    return Pair(paths[0], paths[1])
}

fun calculateMinimalDistance(wires : List<List<String>>) : Int? {
    val (path0, path1) = calculatePath(wires)
    return path0.intersect(path1).map { manhattanDistance(it) }.min()
}

fun calculateMinimalSteps(wires : List<List<String>>) : Int? {
    val (path0, path1) = calculatePath(wires)
    val i0 = path0.intersect(path1).toSortedSet()
    val i1 = path1.intersect(path0).toSortedSet()

    return i0.zip(i1).map{ (a, b) -> a.steps + b.steps}.min()
}

fun loadWires(fileName : String) : List<List<String>> {
    return File(fileName).readLines().map { it.split(",")}.toList()
}

fun part01() {
    val wires: List<List<String>> = loadWires("day_03/input.txt")
    println("Minimum distance = ${calculateMinimalDistance(wires)}")
}

fun part02() {
    val wires: List<List<String>> = loadWires("day_03/input.txt")
    println("Minimum distance = ${calculateMinimalSteps(wires)}")
}

fun main() {
    testExample0()
    testExample1()
    testExample2()

    println("Time Part 01 (ms): ${measureTimeMillis { part01() }}")
    println("Time Part 02 (ms): ${measureTimeMillis { part02() }}")
}

fun testExample0() {
    val wires : List<List<String>> = listOf(
            "R8,U5,L5,D3".split(","),
            "U7,R6,D4,L4".split(","))
    assertEquals(6, calculateMinimalDistance(wires))
    assertEquals(30, calculateMinimalSteps(wires))
}

fun testExample1() {
    val wires : List<List<String>> = listOf(
            "R75,D30,R83,U83,L12,D49,R71,U7,L72".split(","),
            "U62,R66,U55,R34,D71,R55,D58,R83".split(","))
    assertEquals(159, calculateMinimalDistance(wires))
    assertEquals(610, calculateMinimalSteps(wires))
}

fun testExample2() {
    val wires : List<List<String>> = listOf(
            "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51".split(","),
            "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7".split(","))
    assertEquals(135, calculateMinimalDistance(wires))
    assertEquals(410, calculateMinimalSteps(wires))
}
