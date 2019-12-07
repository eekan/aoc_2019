import java.io.File
import java.lang.IllegalStateException
import kotlin.test.assertEquals

fun testExample0() {
    val program : MutableList<Int> = mutableListOf(1,0,0,0,99)
    val expected : MutableList<Int> = mutableListOf(2,0,0,0,99)
    assertEquals(expected, Computer().execute(program))
}

fun testExample1() {
    val program : MutableList<Int> = mutableListOf(2,3,0,3,99)
    val expected : MutableList<Int> = mutableListOf(2,3,0,6,99)
    assertEquals(expected, Computer().execute(program))
}

fun testExample2() {
    val program : MutableList<Int> = mutableListOf(2,4,4,5,99,0)
    val expected : MutableList<Int> = mutableListOf(2,4,4,5,99,9801)
    assertEquals(expected, Computer().execute(program))
}

fun testExample3() {
    val program : MutableList<Int> = mutableListOf(1,1,1,4,99,5,6,0,99)
    val expected : MutableList<Int> = mutableListOf(30,1,1,4,2,5,6,0,99)
    assertEquals(expected, Computer().execute(program))
}

fun testExample4() {

    val program : MutableList<Int> = mutableListOf(1002,4,3,4,33)
    val expected : MutableList<Int> = mutableListOf(1002,4,3,4,99)
    assertEquals(expected, Computer().execute(program))
}

fun testParameterMode() {
    var program = mutableListOf<Int>(761002, 1, 2, 3, 4, 5, 7, 8, 9, 10)
    assertEquals(0, program.getParameterMode(0, 1))
    assertEquals(1, program.getParameterMode(0, 2))
    assertEquals(6, program.getParameterMode(0, 3))
    assertEquals(7, program.getParameterMode(0, 4))
    assertEquals(0, program.getParameterMode(0, 5))
}

fun testIOInstruction() {
    var program = mutableListOf<Int>(3,0,4,0,99)
    Computer().execute(program)
}

fun tests() {
    testExample0()
    testExample1()
    testExample2()
    testExample3()
    testExample4()
    testParameterMode()
    testIOInstruction()
}

fun loadProgram(fileName : String) : List<Int> {
    return File(fileName).readText().trim().split(",").map { it.toInt() }.toList()
}

fun part01() {
    var program = loadProgram("day_05/input.txt").toMutableList()
    Computer().execute(program)
}

fun main() : Unit {
    //tests()
    part01()
}