import java.io.File
import java.lang.IllegalStateException
import kotlin.test.assertEquals

fun MutableList<Int>.store(addressPointer : Int, value : Int) {
    this[this[addressPointer]] = value
}

fun MutableList<Int>.param(paramPointer : Int): Int {
    return this[this[paramPointer]]
}

fun MutableList<Int>.param2(instructionPointer : Int): Int {
    return this[this[instructionPointer + 2]]
}

fun execute(program : MutableList<Int>) : MutableList<Int> {
    var instructionPointer : Int = 0

    while (instructionPointer < program.size) {
        when (val opCode = program[instructionPointer]) {
            1 -> program.store(instructionPointer + 3, program.param(instructionPointer + 1) + program.param(instructionPointer + 2))
            2 -> program.store(instructionPointer + 3, program.param(instructionPointer + 1) * program.param(instructionPointer + 2))
            99 -> return program
            else -> throw IllegalStateException("Unknown opcode encountered: $opCode")
        }
        instructionPointer += 4
    }
    throw IllegalStateException("Execution ended unexpectedly")
}

fun testExample0() {
    val program : MutableList<Int> = mutableListOf(1,0,0,0,99)
    val expected : MutableList<Int> = mutableListOf(2,0,0,0,99)
    assertEquals(expected, execute(program))
}

fun testExample1() {
    val program : MutableList<Int> = mutableListOf(2,3,0,3,99)
    val expected : MutableList<Int> = mutableListOf(2,3,0,6,99)
    assertEquals(expected, execute(program))
}

fun testExample2() {
    val program : MutableList<Int> = mutableListOf(2,4,4,5,99,0)
    val expected : MutableList<Int> = mutableListOf(2,4,4,5,99,9801)
    assertEquals(expected, execute(program))
}

fun testExample3() {
    val program : MutableList<Int> = mutableListOf(1,1,1,4,99,5,6,0,99)
    val expected : MutableList<Int> = mutableListOf(30,1,1,4,2,5,6,0,99)
    assertEquals(expected, execute(program))
}

fun loadProgram(fileName : String) : List<Int> {
    return File(fileName).readText().trim().split(",").map { it.toInt() }.toList()
}

fun tests() {
    testExample0()
    testExample1()
    testExample2()
    testExample3()
}

fun part01() {
    var program = loadProgram("day_02/input.txt").toMutableList()
    // Fix the program before running it
    program[1] = 12
    program[2] = 2

    val result = execute(program)[0]

    println("Result Part 01 (after execution): $result")
}

fun part02() {
    val sourceProgram = loadProgram("day_02/input.txt")

    for (noun in 0..99) {
        for (verb in 0..99) {
            var program = sourceProgram.toMutableList()
            program[1] = noun
            program[2] = verb
            val result = execute(program)[0]
            if (result == 19690720) {
                println("Result Part 02: 100 * $noun + $verb = ${100 * noun + verb}")
                return
            }
        }
    }
    throw IllegalStateException("Failed to find noun, verb that satisfies the requirements")
}

fun main() : Unit {
    tests()
    part01()
    part02()
}