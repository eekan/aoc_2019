
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.BlockingQueue

typealias Program = MutableList<Long>

class Computer(input: BlockingQueue<Long>, output : BlockingQueue<Long>){
    private var inputQueue = input
    private var outputQueue = output
    private var relativeBase: Long = 0
    private var memory = Memory(mutableListOf())

    private fun add(instructionPointer: Long): Long {
        memory.store(instructionPointer, 3, relativeBase,
                memory.getParameter(instructionPointer, 1, relativeBase)
                        + memory.getParameter(instructionPointer, 2, relativeBase))
        return instructionPointer + 4
    }

    private fun mul(instructionPointer: Long) : Long {
        memory.store(instructionPointer, 3, relativeBase,
                memory.getParameter(instructionPointer, 1, relativeBase)
                        * memory.getParameter(instructionPointer, 2, relativeBase))
        return instructionPointer + 4
    }

    private fun readInput() : Long {
        var value = inputQueue.take()
        println("> give number: $value")
        return value
    }

    private fun giveOutput(number : Long) {
        println(number)
        outputQueue.put(number)
    }

    private fun input(instructionPointer: Long): Long {
        memory.store(instructionPointer, 1,  relativeBase, readInput())
        return instructionPointer + 2
    }

    private fun output(instructionPointer: Long): Long {
        giveOutput(memory.getParameter(instructionPointer, 1, relativeBase))
        return instructionPointer + 2
    }

    private fun jumpIf(instructionPointer: Long, jumpIfZero: Boolean): Long {
        val isZero = memory.getParameter(instructionPointer, 1, relativeBase) == 0L

        return if (isZero == jumpIfZero)
            memory.getParameter(instructionPointer, 2, relativeBase)
        else
            instructionPointer + 3
    }

    private fun lessThan(instructionPointer: Long): Long {
        val value =
                if (memory.getParameter(instructionPointer, 1, relativeBase)
                    < memory.getParameter(instructionPointer, 2, relativeBase)) 1L else 0

        memory.store(instructionPointer, 3, relativeBase, value)
        return instructionPointer + 4
    }

    private fun isEquals(instructionPointer: Long): Long {
        val value =
                if (memory.getParameter(instructionPointer, 1, relativeBase)
                        == memory.getParameter(instructionPointer, 2, relativeBase)) 1L else 0

        memory.store(instructionPointer, 3, relativeBase, value)
        return instructionPointer + 4
    }

    private fun adjustRelativeBase(instructionPointer: Long): Long {
        relativeBase += memory.getParameter(instructionPointer, 1, relativeBase)
        return instructionPointer + 2
    }

    fun execute(program : Program) : Program {
        var instructionPointer : Long = 0
        memory = Memory(program)
        relativeBase = 0

        while (instructionPointer < program.size) {
            instructionPointer = when (val opCode = memory.getOpCode(instructionPointer)) {
                1L -> add(instructionPointer)
                2L -> mul(instructionPointer)
                3L -> input(instructionPointer)
                4L -> output(instructionPointer)
                5L -> jumpIf(instructionPointer, false)
                6L -> jumpIf(instructionPointer, true)
                7L -> lessThan(instructionPointer)
                8L -> isEquals(instructionPointer)
                9L -> adjustRelativeBase(instructionPointer)
                99L -> return memory.toProgram()
                else -> throw IllegalStateException("Unknown opcode encountered: $opCode")
            }
        }
        throw IllegalStateException("Execution ended unexpectedly")
    }
}