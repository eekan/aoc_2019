import java.lang.IllegalStateException
import java.util.concurrent.BlockingQueue

typealias Program = MutableList<Long>
const val END_OF_QUEUE = Long.MIN_VALUE

class Computer(input: BlockingQueue<Long>, output : BlockingQueue<Long>, program: Program){
    private var inputQueue = input
    private var outputQueue = output
    private var relativeBase: Long = 0
    private var memory = Memory(program.toMutableList())
    private var instructionPointer : Long = 0
    @Volatile private var stop: Boolean = false

    private fun add(ip: Long): Long {
        memory.store(ip, 3, relativeBase,
                memory.getParameter(ip, 1, relativeBase)
                        + memory.getParameter(ip, 2, relativeBase))
        return ip + 4
    }

    private fun mul(ip: Long) : Long {
        memory.store(ip, 3, relativeBase,
                memory.getParameter(ip, 1, relativeBase)
                        * memory.getParameter(ip, 2, relativeBase))
        return ip + 4
    }

    private fun readInput() : Long {
        var value = inputQueue.take()
        //println("> give number: $value")
        return value
    }

    private fun giveOutput(number : Long) {
        //println("OUTPUT: $number")
        outputQueue.put(number)
    }

    private fun input(ip: Long): Long {
        val value =
        memory.store(ip, 1,  relativeBase, readInput())
        return ip + 2
    }

    private fun output(ip: Long): Long {
        giveOutput(memory.getParameter(ip, 1, relativeBase))
        return ip + 2
    }

    private fun jumpIf(ip: Long, jumpIfZero: Boolean): Long {
        val isZero = memory.getParameter(ip, 1, relativeBase) == 0L

        return if (isZero == jumpIfZero)
            memory.getParameter(ip, 2, relativeBase)
        else
            ip + 3
    }

    private fun lessThan(ip: Long): Long {
        val value =
                if (memory.getParameter(ip, 1, relativeBase)
                        < memory.getParameter(ip, 2, relativeBase)) 1L else 0

        memory.store(ip, 3, relativeBase, value)
        return ip + 4
    }

    private fun isEquals(ip: Long): Long {
        val value =
                if (memory.getParameter(ip, 1, relativeBase)
                        == memory.getParameter(ip, 2, relativeBase)) 1L else 0

        memory.store(ip, 3, relativeBase, value)
        return ip + 4
    }

    private fun adjustRelativeBase(ip: Long): Long {
        relativeBase += memory.getParameter(ip, 1, relativeBase)
        return ip + 2
    }

    fun execute() : Long {
        stop = false
        while (!stop) {
            instructionPointer = when (val opCode = memory.getOpCode(instructionPointer)) {
                1L -> add(instructionPointer)
                2L -> mul(instructionPointer)
                3L -> if (inputQueue.peek() == null) {
                    return -1L
                } else {
                    input(instructionPointer)
                }
                4L -> output(instructionPointer)
                5L -> jumpIf(instructionPointer, false)
                6L -> jumpIf(instructionPointer, true)
                7L -> lessThan(instructionPointer)
                8L -> isEquals(instructionPointer)
                9L -> adjustRelativeBase(instructionPointer)
                99L -> return 99L
                else -> throw IllegalStateException("Unknown opcode encountered: $opCode at $instructionPointer")
            }
        }

        return 0L
    }

    fun stop() {
        stop = true
    }

    fun addInput(input: Long) {
        inputQueue.put(input)
    }

    fun getAllOutput(): List<Long> {
        val res = outputQueue.toList()
        outputQueue.clear()
        return res
    }
}