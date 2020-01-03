import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.system.measureTimeMillis

// Not blocking blocking queue...
// TODO remove this,, use blocking one and let router do this
class InputQueue: LinkedBlockingQueue<Long>() {
    private var next: Long? = null
    private var polled = false

    override fun take(): Long {
        if (next != null) {
            val ret = checkNotNull(next)
            next = null
            polled = false
            return ret
        } else if (super.size >= 2) {
            val ret = super.take()
            next = super.take()
            polled = false
            return ret
        }
        polled = true
        return -1
    }

    override fun peek(): Long {
        return super.peek() ?: -1
    }

    fun isPolled(): Boolean {
        return isEmpty() && polled
    }
}

fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

data class NetworkNode(val computer: Computer, val input: BlockingQueue<Long>, val output: BlockingQueue<Long>)
data class Package(val destination: Int, val x: Long, val y: Long)

fun createNodes(n: Int, program: Program): List<NetworkNode> {
    val nodes = List(n) {
        val input = LinkedBlockingQueue<Long>()
        val output = LinkedBlockingQueue<Long>()
        NetworkNode(Computer(input, output, program.toMutableList()), input, output)
    }

    // Set network addresses
    nodes.forEachIndexed { index, node ->
        node.input.put(index.toLong())
    }

    return nodes
}

fun part01() {
    var program = loadProgram("day_23/input.txt")
    val nodes = createNodes(50, program.toMutableList())


    while (true) {
        val nodesWithOutput = nodes.filter { it.output.peek() != null }
        val inputs = nodesWithOutput.map {
            Package(it.output.take().toInt(), it.output.take(), it.output.take())
        }

        // Forward messages
        val hasInput = mutableSetOf<Int>()
        inputs.forEach { (destination, x, y) ->
            if (destination < nodes.size) {
                nodes[destination].input.put(x)
                nodes[destination].input.put(y)

                hasInput.add(destination)
            }

            // What is the Y value of the first packet sent to address 255?
            if (destination == 255) {
                println("[Part 01] output: $y")
                return
            }
        }

        nodes.forEachIndexed{ index, node ->
            if (index !in inputs.map { i -> i.destination })
                node.input.put(-1)
            node.computer.execute() }
    }
}

fun part02() {
    var program = loadProgram("day_23/input.txt")
    val nodes = createNodes(50, program.toMutableList())
    var natPackage: Package? = null
    var lastNatPackage = Package(0, 0, 0)
    var lastIdle = false

    while (true) {
        val nodesWithOutput = nodes.filter { it.output.peek() != null }
        val inputs = nodesWithOutput.map {
                Package(it.output.take().toInt(), it.output.take(), it.output.take())
            }

        // Forward messages
        val hasInput = mutableSetOf<Int>()
        inputs.forEach { (destination, x, y) ->
            if (destination < nodes.size) {
                nodes[destination].input.put(x)
                nodes[destination].input.put(y)

                hasInput.add(destination)
            }

            if (destination == 255) {
               natPackage = Package(destination, x, y)
            }
        }

        if (inputs.isEmpty() && natPackage != null) {
            if (natPackage == lastNatPackage) {
                println("[Part 02] output: ${natPackage!!.y}")
                return
            } else {
                nodes[0].input.put(natPackage!!.x)
                nodes[0].input.put(natPackage!!.y)
            }
            lastNatPackage = natPackage!!
        }

        nodes.forEachIndexed { index, node ->
            if (index !in inputs.map { i -> i.destination })
                node.input.put(-1)
            node.computer.execute()
        }
    }
}

fun main() {
    println("[Part 01] time=${measureTimeMillis { part01() }}")
    println("[Part 02] time=${measureTimeMillis { part02() }}")
}