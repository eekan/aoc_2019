
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.BlockingQueue

class Computer(input: BlockingQueue<Int>, output : BlockingQueue<Int>){
    private var inputQueue = input
    private var outputQueue = output

    private fun add(program: Program, instructionPointer: Int): Int {
        program.store(instructionPointer, 3,
                program.getParameter(instructionPointer, 1)
                        + program.getParameter(instructionPointer, 2))
        return instructionPointer + 4
    }

    private fun mul(program: Program, instructionPointer: Int) : Int {
        program.store(instructionPointer, 3,
                program.getParameter(instructionPointer, 1)
                        * program.getParameter(instructionPointer, 2))
        return instructionPointer + 4
    }

    private fun readInput() : Int {
        var value = inputQueue.take()
        println("> give number: $value")
        return value
    }

    private fun giveOutput(number : Int) {
        println(number)
        outputQueue.put(number)
    }

    private fun input(program: Program, instructionPointer: Int): Int {
        program.store(instructionPointer, 1, readInput())
        return instructionPointer + 2
    }

    private fun output(program: Program, instructionPointer: Int): Int {
        giveOutput(program.getParameter(instructionPointer, 1))
        return instructionPointer + 2
    }

    private fun jumpIf(program: Program, instructionPointer: Int, jumpIfZero: Boolean): Int {
        val isZero = program.getParameter(instructionPointer, 1) == 0

        return if (isZero == jumpIfZero)
            program.getParameter(instructionPointer, 2)
        else
            instructionPointer + 3
    }

    private fun lessThan(program: Program, instructionPointer: Int): Int {
        val value =
                if (program.getParameter(instructionPointer, 1)
                    < program.getParameter(instructionPointer, 2)) 1 else 0

        program.store(instructionPointer, 3, value)
        return instructionPointer + 4
    }

    private fun isEquals(program: Program, instructionPointer: Int): Int {
        val value =
                if (program.getParameter(instructionPointer, 1)
                        == program.getParameter(instructionPointer, 2)) 1 else 0

        program.store(instructionPointer, 3, value)
        return instructionPointer + 4
    }

    fun execute(program : Program) : Program {
        var instructionPointer : Int = 0

        while (instructionPointer < program.size) {
            instructionPointer = when (val opCode = program.getOpCode(instructionPointer)) {
                1 -> add(program, instructionPointer)
                2 -> mul(program, instructionPointer)
                3 -> input(program, instructionPointer)
                4 -> output(program, instructionPointer)
                5 -> jumpIf(program, instructionPointer, false)
                6 -> jumpIf(program, instructionPointer, true)
                7 -> lessThan(program, instructionPointer)
                8 -> isEquals(program, instructionPointer)
                99 -> return program
                else -> throw IllegalStateException("Unknown opcode encountered: $opCode")
            }
        }
        throw IllegalStateException("Execution ended unexpectedly")
    }
}