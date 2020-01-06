import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import kotlin.system.measureTimeMillis

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun List<Long>.renderOutput() =
        joinToString(separator = "") { if (it < 128) it.toChar().toString() else it.toString() }

fun Computer.putAscii(s: String) {
    (s+"\n").forEach { addInput(it.toLong()) }
}

enum class Direction {
    North, South, West, East;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}

fun Direction.reverse() = when (this) {
    Direction.North -> Direction.South
    Direction.South -> Direction.North
    Direction.East -> Direction.West
    Direction.West -> Direction.East
}

data class Node(val name: String, val items: List<String>, val path: List<Direction>, val doors: List<Direction>)

fun String.parseNode(path: List<Direction>): Node {
    val lines = split("\n")

    var name = ""
    val items = mutableListOf<String>()
    val doors = mutableListOf<Direction>()

    for ((index, line) in lines.withIndex()) {
        if (line.startsWith("== ")) {
            name = line.substring(3, line.lastIndex - 2).trim()

        } else if (line.startsWith("Doors here lead:")) {
            for (l in lines.drop(index + 1)) {
                if (l =="- north") {
                    doors.add(Direction.North)
                } else if(l == "- south") {
                    doors.add(Direction.South)
                } else if(l == "- east") {
                    doors.add(Direction.East)
                } else if(l == "- west") {
                    doors.add(Direction.West)
                } else {
                    break
                }
            }
        } else if (line.startsWith("Items here:")) {
            for (l in lines.drop(index + 1)) {
                if (!l.startsWith("-")) break
                items.add(l.substring(2))
            }

        }
    }

    return Node(name, items, path, doors)
}

// Explore all nodes
fun explore(c: Computer, path: List<Direction>): List<Node> {
    c.execute()
    val allOutput = c.getAllOutput()

    if (allOutput.isNotEmpty()) {
        val node = allOutput.renderOutput().parseNode(path)

        if (node.name == "Security Checkpoint") {
            // Do not continue from here
            return listOf(node)
        } else {
            val res =
                    (if (path.isEmpty()) node.doors else node.doors.filter { it != path.last().reverse() })
                            .flatMap { d ->
                                c.putAscii(d.toString()) // Go to
                                val value = explore(c, path + d)

                                // Reverse back to node
                                c.putAscii(d.reverse().toString())
                                c.execute()
                                c.getAllOutput()
                                value
                            }

            return res + node
        }
    }

    return listOf()
}

fun Collection<String>.powerSet() : Set<Set<String>> {
    if (isEmpty()) return setOf(emptySet())
    return drop(1).powerSet().let { it + it.map { s -> s + first() } }
}

fun isSafe(c: Computer, itemsMap: Map<String, List<Direction>>, item: String): Boolean {
    // Dangerous items
    if (item in listOf("infinite loop", "giant electromagnet")) return false

    // Go to item
    itemsMap[item]!!.forEach { dir ->
        c.putAscii(dir.toString())
    }

    // Try take item
    c.putAscii("take $item")
    return c.execute() != 99L
}

fun check(c: Computer, itemsMap: Map<String, List<Direction>>,
          items: Set<String>, gatePath: List<Direction>): String {
    // Pickup each item
    items.forEach {
        // Go to item
        itemsMap[it]!!.forEach { dir ->
            c.putAscii(dir.toString())
        }

        c.putAscii("take $it")

        // Go back to start pos
        itemsMap[it]!!.reversed().map { dir -> dir.reverse() }.forEach { dir ->
            c.putAscii(dir.toString())
        }
    }

    var res = c.execute()
    c.getAllOutput().renderOutput()
    if (res == 99L) return ""

    // Move first into security checkpoint (to get cleaner output)
    gatePath.dropLast(1).forEach { c.putAscii(it.toString()) }
    c.execute()
    c.getAllOutput().renderOutput()

    c.putAscii(gatePath.last().toString())
    c.execute()

    var output = c.getAllOutput().renderOutput()
    if (output.contains("Analysis complete")) return output
    return ""
}

fun part01() {
    var program = loadProgram("day_25/input.txt")

    val inputQueue = LinkedBlockingQueue<Long>()
    val outputQueue = LinkedBlockingQueue<Long>()
    val computer = Computer(inputQueue, outputQueue, program.toMutableList())

    val nodes = explore(computer, listOf())
    val securityCheckpoint = nodes.first { it.name == "Security Checkpoint" }
    val gatePath = securityCheckpoint.path + securityCheckpoint.doors.first() { it != securityCheckpoint.path.last().reverse() }
    val itemsMap = nodes.flatMap { it.items.map { item -> item to it.path } }.toMap()
    val safeItems = itemsMap.keys.filter {
        isSafe(Computer(LinkedBlockingQueue<Long>(), LinkedBlockingQueue<Long>(), program.toMutableList()), itemsMap, it)
    }

    for (items in safeItems.powerSet()) {
        val c = Computer(LinkedBlockingQueue<Long>(), LinkedBlockingQueue<Long>(), program.toMutableList())
        val res = check(c, itemsMap, items, gatePath)
        if (res.isNotEmpty()) {
            println("Items = $items")
            println(res.trim())
            break
        }
    }
}

fun main() {
    println("[Part 01] time=${measureTimeMillis { part01() }}")
}