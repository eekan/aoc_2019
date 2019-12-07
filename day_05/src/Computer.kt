
import java.lang.IllegalStateException



class Computer(){
    private var interactiveMode: Boolean = true
    private var inputQueue = Queue<Int>()
    private var outputQueue = Queue<Int>()

    constructor(input : List<Int>) : this() {
        interactiveMode = false
        inputQueue = Queue(input)
    }

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

    private fun readInputNonInteractive() : Int {
        if (inputQueue.size == 0)
            throw IllegalStateException("Running in non-interactive mode, enough input was not given")
        var value = inputQueue.pop()
        println("> give number: $value")
        return value
    }

    private fun readInputInteractive() : Int {
        while (true) {
            print("> give number: ")
            try {
                return Integer.valueOf(readLine())
            } catch (e: java.lang.NumberFormatException) {
                println("Input was not a number, try again!")
            }
        }
    }

    private fun readInput() : Int {
       return if (interactiveMode) readInputInteractive() else readInputNonInteractive()
    }

    private fun printOutput(number : Int) {
        outputQueue.push(number)
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

    fun getOutput(): List<Int> {
        return outputQueue.toList()
    }
}