
import java.lang.IllegalStateException



class Computer {
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
        while (true) {
            print("> give number: ")
            try {
                return Integer.valueOf(readLine())
            } catch (e : java.lang.NumberFormatException) {
                println("Input was not a number, try again!")
            }
        }
    }

    private fun printOutput(number : Int) {
        println(number)
    }

    private fun input(program: Program, instructionPointer: Int): Int {
        program.store(instructionPointer, 1, readInput())
        return instructionPointer + 2
    }

    private fun output(program: Program, instructionPointer: Int): Int {
        printOutput(program.getParameter(instructionPointer, 1))
        return instructionPointer + 2
    }

    fun execute(program : Program) : Program {
        var instructionPointer : Int = 0

        while (instructionPointer < program.size) {
            instructionPointer = when (val opCode = program.getOpCode(instructionPointer)) {
                1 -> add(program, instructionPointer)
                2 -> mul(program, instructionPointer)
                3 -> input(program, instructionPointer)
                4 -> output(program, instructionPointer)
                99 -> return program
                else -> throw IllegalStateException("Unknown opcode encountered: $opCode")
            }
        }
        throw IllegalStateException("Execution ended unexpectedly")
    }
}