import java.io.File
import java.util.*
import kotlin.collections.HashSet
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

fun loadInput(fileName : String) : List<List<Char>> {
    return File(fileName).readLines().map { it.toCharArray().toList() }
}

fun List<List<Char>>.toXYMap(): MutableMap<Coordinate, Char> {
    return mapIndexed { row, list ->
        list.mapIndexed{ col, value ->
            Coordinate(col, row, 0) to value
        }
    }.flatten().toMap().filterValues { it == '.' || it in 'A'..'Z' }.toMutableMap()
}

data class Coordinate(val x: Int, val y: Int, val z: Int)
operator fun Coordinate.plus(that: Coordinate) =
        Coordinate(this.x + that.x, this.y + that.y, this.z + that.z)
operator fun Coordinate.compareTo(that: Coordinate): Int {
    var res = this.x.compareTo(that.x)
    if (res != 0) return res
    res = this.y.compareTo(that.y)
    if (res != 0) return res
    return this.z.compareTo(that.z)
}

fun getLabels(input: Map<Coordinate, Char>): Map<String, List<Coordinate>> {
    fun Coordinate.toLabel(a: Coordinate, b: Coordinate) =
            if (input[this + a] == '.' && input[this + b] in 'A'..'Z') {
                if (a < b) Pair(input[this].toString() + input[this + b], this + a)
                else Pair(input[this + b].toString() + input[this], this + a)
            } else null

    return input.filterValues { it in 'A' .. 'Z' }
            .mapNotNull { (cord, value) ->
                cord.toLabel(Coordinate(0, -1, 0), Coordinate(0, 1, 0)) ?:
                cord.toLabel(Coordinate(0, 1, 0), Coordinate(0, -1, 0)) ?:
                cord.toLabel(Coordinate(-1, 0, 0), Coordinate(1, 0, 0)) ?:
                cord.toLabel(Coordinate(1, 0, 0), Coordinate(-1, 0, 0))
            }
            .groupBy { it.first }
            .mapValues { (_, labels) ->
                labels.map { it.second }
            }
}

fun getPortals(labels: Map<String, List<Coordinate>>): Map<Coordinate, Coordinate> {
    return labels.values.filter {
        it.size == 2
    }.flatMap { (a, b) ->
        listOf(a to b, b to a)
    }.toMap()
}

fun Map<Coordinate, Char>.bounds(symbols: String = "."): Pair<List<Int>, List<Int>> {
    val cleaned = filterValues { it in symbols }
    val xMin = cleaned.keys.minBy { it.x }?.x ?: throw IllegalArgumentException("Maze missing elements!")
    val xMax = cleaned.keys.maxBy { it.x }?.x ?: throw IllegalArgumentException("Maze missing elements!")
    val yMin = cleaned.keys.minBy { it.y }?.y ?: throw IllegalArgumentException("Maze missing elements!")
    val yMax = cleaned.keys.maxBy { it.y }?.y ?: throw IllegalArgumentException("Maze missing elements!")
    return Pair(listOf(xMin, xMax), listOf(yMin, yMax))
}

fun Map<Coordinate, Coordinate>.getPortal(c: Coordinate) =
        this[Coordinate(c.x, c.y, 0)]?.plus(Coordinate(0,0, c.z))

fun Map<Coordinate, Char>.canWalk(c: Coordinate) = this[Coordinate(c.x, c.y, 0)] == '.'

fun bfs(maze: Map<Coordinate, Char>, portals: Map<Coordinate,Coordinate>, start: Coordinate, end: Coordinate): Int {
    val queue = ArrayDeque<Pair<Coordinate, Int>>()
    val visited = HashSet<Coordinate>()
    val directions = listOf(
            Coordinate(0, 1, 0),
            Coordinate(1, 0, 0),
            Coordinate(0, -1, 0),
            Coordinate(-1, 0, 0))
    val maxLevel = portals.size / 2 + 1

    queue.add(Pair(start, 0))

    while (queue.isNotEmpty()) {
        val (position, steps) = queue.remove()

        if (position == end) return steps

        val nextPositions = directions.map { position + it } + portals.getPortal(position)
        for (nextPosition in nextPositions.filterNotNull()) {

            if (nextPosition.z > maxLevel || nextPosition.z < 0) continue
            if (!maze.canWalk(nextPosition)) continue
            if (!visited.add(nextPosition)) continue

            queue.add(Pair(nextPosition, steps + 1))
        }
    }

    return -1
}

fun solve(xyMap: Map<Coordinate, Char>): Int {
    val labels = getLabels(xyMap)
    val start = labels["AA"]?.first() ?: throw IllegalArgumentException("Maze missing start!")
    val end = labels["ZZ"]?.first() ?: throw IllegalArgumentException("Maze missing start!")

    return bfs(xyMap, getPortals(labels), start, end)
}

fun solve2(xyMap: Map<Coordinate, Char>): Int {
    val labels = getLabels(xyMap)
    val start = labels["AA"]?.first() ?: throw IllegalArgumentException("Maze missing start!")
    val end = labels["ZZ"]?.first() ?: throw IllegalArgumentException("Maze missing start!")

    val bounds = xyMap.bounds()
    fun isOuterPortal(c: Coordinate) = c.x in bounds.first || c.y in bounds.second

    val portals = getPortals(labels).mapValues { (k, v) ->
        Coordinate(v.x, v.y, if (isOuterPortal(k)) -1 else 1)
    }

    return bfs(xyMap, portals, start, end)
}

fun part01() {
    println("[Part 01] score=${solve(loadInput("day_20/input.txt").toXYMap())}")
}

fun part02() {
    println("[Part 02] score=${solve2(loadInput("day_20/input.txt").toXYMap())}")
}

fun main() {
    testsPart01()
    testsPart02()
    println("[Part 01] time=${measureTimeMillis { part01() }}")
    println("[Part 02] time=${measureTimeMillis { part02() }}")
}

fun testsPart01() {
    println("[Example 1] time=${measureTimeMillis { example1() }}")
    println("[Example 2] time=${measureTimeMillis { example2() }}")
}

fun testsPart02() {
    println("[Example 3] time=${measureTimeMillis { example3() }}")
    println("[Example 4] time=${measureTimeMillis { example4() }}")
    println("[Example 5] time=${measureTimeMillis { example5() }}")
}

fun example1() {
    val input = listOf(
        "         A         ",
        "         A         ",
        "  #######.#########",
        "  #######.........#",
        "  #######.#######.#",
        "  #######.#######.#",
        "  #######.#######.#",
        "  #####  B    ###.#",
        "BC...##  C    ###.#",
        "  ##.##       ###.#",
        "  ##...DE  F  ###.#",
        "  #####    G  ###.#",
        "  #########.#####.#",
        "DE..#######...###.#",
        "  #.#########.###.#",
        "FG..#########.....#",
        "  ###########.#####",
        "             Z     ",
        "             Z     "
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(23, solve(input))
}

fun example2() {
    val input = listOf(
        "                     A             ",
        "                     A             ",
        "    #################.#############",
        "    #.#...#...................#.#.#",
        "    #.#.#.###.###.###.#########.#.#",
        "    #.#.#.......#...#.....#.#.#...#",
        "    #.#########.###.#####.#.#.###.#",
        "    #.............#.#.....#.......#",
        "    ###.###########.###.#####.#.#.#",
        "    #.....#        A   C    #.#.#.#",
        "    #######        S   P    #####.#",
        "    #.#...#                 #......VT",
        "    #.#.#.#                 #.#####",
        "    #...#.#               YN....#.#",
        "    #.###.#                 #####.#",
        "  DI....#.#                 #.....#",
        "    #####.#                 #.###.#",
        "  ZZ......#               QG....#..AS",
        "    ###.###                 #######",
        "  JO..#.#.#                 #.....#",
        "    #.#.#.#                 ###.#.#",
        "    #...#..DI             BU....#..LF",
        "    #####.#                 #.#####",
        "  YN......#               VT..#....QG",
        "    #.###.#                 #.###.#",
        "    #.#...#                 #.....#",
        "    ###.###    J L     J    #.#.###",
        "    #.....#    O F     P    #.#...#",
        "    #.###.#####.#.#####.#####.###.#",
        "    #...#.#.#...#.....#.....#.#...#",
        "    #.#####.###.###.#.#.#########.#",
        "    #...#.#.....#...#.#.#.#.....#.#",
        "    #.###.#####.###.###.#.#.#######",
        "    #.#.........#...#.............#",
        "    #########.###.###.#############",
        "             B   J   C",
        "             U   P   P"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(58, solve(input))
}

fun example3() {
    val input = listOf(
            "         A         ",
            "         A         ",
            "  #######.#########",
            "  #######.........#",
            "  #######.#######.#",
            "  #######.#######.#",
            "  #######.#######.#",
            "  #####  B    ###.#",
            "BC...##  C    ###.#",
            "  ##.##       ###.#",
            "  ##...DE  F  ###.#",
            "  #####    G  ###.#",
            "  #########.#####.#",
            "DE..#######...###.#",
            "  #.#########.###.#",
            "FG..#########.....#",
            "  ###########.#####",
            "             Z     ",
            "             Z     "
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(26, solve2(input))
}

fun example4() {
    val input = listOf(
            "                     A             ",
            "                     A             ",
            "    #################.#############",
            "    #.#...#...................#.#.#",
            "    #.#.#.###.###.###.#########.#.#",
            "    #.#.#.......#...#.....#.#.#...#",
            "    #.#########.###.#####.#.#.###.#",
            "    #.............#.#.....#.......#",
            "    ###.###########.###.#####.#.#.#",
            "    #.....#        A   C    #.#.#.#",
            "    #######        S   P    #####.#",
            "    #.#...#                 #......VT",
            "    #.#.#.#                 #.#####",
            "    #...#.#               YN....#.#",
            "    #.###.#                 #####.#",
            "  DI....#.#                 #.....#",
            "    #####.#                 #.###.#",
            "  ZZ......#               QG....#..AS",
            "    ###.###                 #######",
            "  JO..#.#.#                 #.....#",
            "    #.#.#.#                 ###.#.#",
            "    #...#..DI             BU....#..LF",
            "    #####.#                 #.#####",
            "  YN......#               VT..#....QG",
            "    #.###.#                 #.###.#",
            "    #.#...#                 #.....#",
            "    ###.###    J L     J    #.#.###",
            "    #.....#    O F     P    #.#...#",
            "    #.###.#####.#.#####.#####.###.#",
            "    #...#.#.#...#.....#.....#.#...#",
            "    #.#####.###.###.#.#.#########.#",
            "    #...#.#.....#...#.#.#.#.....#.#",
            "    #.###.#####.###.###.#.#.#######",
            "    #.#.........#...#.............#",
            "    #########.###.###.#############",
            "             B   J   C",
            "             U   P   P"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(-1, solve2(input))
}

fun example5() {
    val input = listOf(
            "             Z L X W       C",
            "             Z P Q B       K",
            "  ###########.#.#.#.#######.###############",
            "  #...#.......#.#.......#.#.......#.#.#...#",
            "  ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###",
            "  #.#...#.#.#...#.#.#...#...#...#.#.......#",
            "  #.###.#######.###.###.#.###.###.#.#######",
            "  #...#.......#.#...#...#.............#...#",
            "  #.#########.#######.#.#######.#######.###",
            "  #...#.#    F       R I       Z    #.#.#.#",
            "  #.###.#    D       E C       H    #.#.#.#",
            "  #.#...#                           #...#.#",
            "  #.###.#                           #.###.#",
            "  #.#....OA                       WB..#.#..ZH",
            "  #.###.#                           #.#.#.#",
            "CJ......#                           #.....#",
            "  #######                           #######",
            "  #.#....CK                         #......IC",
            "  #.###.#                           #.###.#",
            "  #.....#                           #...#.#",
            "  ###.###                           #.#.#.#",
            "XF....#.#                         RF..#.#.#",
            "  #####.#                           #######",
            "  #......CJ                       NM..#...#",
            "  ###.#.#                           #.###.#",
            "RE....#.#                           #......RF",
            "  ###.###        X   X       L      #.#.#.#",
            "  #.....#        F   Q       P      #.#.#.#",
            "  ###.###########.###.#######.#########.###",
            "  #.....#...#.....#.......#...#.....#.#...#",
            "  #####.#.###.#######.#######.###.###.#.#.#",
            "  #.......#.......#.#.#.#.#...#...#...#.#.#",
            "  #####.###.#####.#.#.#.#.###.###.#.###.###",
            "  #.......#.....#.#...#...............#...#",
            "  #############.#.#.###.###################",
            "               A O F   N",
            "               A A D   M"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(396, solve2(input))
}