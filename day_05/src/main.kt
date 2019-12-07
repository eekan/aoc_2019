import java.io.File
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

fun testExample5() {
    // Output 1 if input is 8, else 0
    val program = mutableListOf(3,9,8,9,10,9,4,9,99,-1,8)
    var computer = Computer(listOf(8))
    computer.execute(program)
    assertEquals(listOf(1), computer.getOutput())

    computer = Computer(listOf(7))
    computer.execute(program)
    assertEquals(listOf(0), computer.getOutput())
}

fun testExample6() {
    // Output 1 if input is less than 8, else 0
    val program = mutableListOf(3,9,7,9,10,9,4,9,99,-1,8)
    var computer = Computer(listOf(8))
    computer.execute(program)
    assertEquals(listOf(0), computer.getOutput())

    computer = Computer(listOf(7))
    computer.execute(program)
    assertEquals(listOf(1), computer.getOutput())
}

fun testExample7() {
    // Output 1 if input is equal 8, else 0
    val program = mutableListOf(3,3,1108,-1,8,3,4,3,99)
    var computer = Computer(listOf(8))
    computer.execute(program)
    assertEquals(listOf(1), computer.getOutput())

    computer = Computer(listOf(7))
    computer.execute(program)
    assertEquals(listOf(0), computer.getOutput())
}

fun testExample8() {
    // Output 1 if input is less than 8, else 0
    val program = mutableListOf(3,3,1107,-1,8,3,4,3,99)
    var computer = Computer(listOf(8))
    computer.execute(program)
    assertEquals(listOf(0), computer.getOutput())

    computer = Computer(listOf(7))
    computer.execute(program)
    assertEquals(listOf(1), computer.getOutput())
}

fun testExample9() {
    // Output 0 if input is 0, else 1
    var program = mutableListOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9)
    var computer = Computer(listOf(1))
    computer.execute(program)
    assertEquals(listOf(1), computer.getOutput())

    program = mutableListOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9)
    computer = Computer(listOf(0))
    computer.execute(program)
    assertEquals(listOf(0), computer.getOutput())
}

fun testExample10() {
    // Output 0 if input is 0, else 1
    var program = mutableListOf(3,3,1105,-1,9,1101,0,0,12,4,12,99,1)
    var computer = Computer(listOf(1))
    computer.execute(program)
    assertEquals(listOf(1), computer.getOutput())

    computer = Computer(listOf(0))
    computer.execute(program)
    assertEquals(listOf(0), computer.getOutput())
}

fun testExample11() {
    //  The program will then output 999 if the input value is below 8,
    //  output 1000 if the input value is equal to 8,
    //  or output 1001 if the input value is greater than 8.
    val program = mutableListOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
            1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
            999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
    var computer = Computer(listOf(7))
    computer.execute(program)
    assertEquals(listOf(999), computer.getOutput())

    computer = Computer(listOf(8))
    computer.execute(program)
    assertEquals(listOf(1000), computer.getOutput())

    computer = Computer(listOf(9))
    computer.execute(program)
    assertEquals(listOf(1001), computer.getOutput())
}

fun testParameterMode() {
    var program = mutableListOf<Int>(761002, 1, 2, 3, 4, 5, 7, 8, 9, 10)
    assertEquals(0, program.getParameterMode(0, 1))
    assertEquals(1, program.getParameterMode(0, 2))
    assertEquals(6, program.getParameterMode(0, 3))
    assertEquals(7, program.getParameterMode(0, 4))
    assertEquals(0, program.getParameterMode(0, 5))
}

fun testIOInstruction(inputValue : Int) {
    var program = mutableListOf<Int>(3,0,4,0,99)
    val computer = Computer(listOf(inputValue))
    computer.execute(program)
    assertEquals(listOf(inputValue), computer.getOutput())
}

fun tests() {
    testExample0()
    testExample1()
    testExample2()
    testExample3()
    testExample4()
    testExample5()
    testExample6()
    testExample7()
    testExample8()
    testExample9()
    testExample10()
    testExample11()
    testParameterMode()
    testIOInstruction(-3)
}

fun loadProgram(fileName : String) : List<Int> {
    return File(fileName).readText().trim().split(",").map { it.toInt() }.toList()
}

fun part01() {
    var program = loadProgram("day_05/input.txt").toMutableList()
    Computer(listOf(1)).execute(program)
}

fun part02() {
    var program = loadProgram("day_05/input.txt").toMutableList()
    Computer(listOf(5)).execute(program)
}

fun main() : Unit {
    //tests()
    //part01()
    part02()

}