import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.test.assertEquals

fun Int.toBlockingQueue(capacity: Int): BlockingQueue<Long> {
    var queue = ArrayBlockingQueue<Long>(capacity)
    queue.add(this.toLong())
    return queue
}

fun testExample0() {
    val program : MutableList<Long> = mutableListOf(1,0,0,0,99)
    val expected : MutableList<Long> = mutableListOf(2,0,0,0,99)
    assertEquals(expected, Computer(ArrayBlockingQueue<Long>(1), ArrayBlockingQueue<Long>(1)).execute(program))
}

fun testExample1() {
    val program : MutableList<Long> = mutableListOf(2,3,0,3,99)
    val expected : MutableList<Long> = mutableListOf(2,3,0,6,99)
    assertEquals(expected, Computer(ArrayBlockingQueue<Long>(1), ArrayBlockingQueue<Long>(1)).execute(program))
}

fun testExample2() {
    val program : MutableList<Long> = mutableListOf(2,4,4,5,99,0)
    val expected : MutableList<Long> = mutableListOf(2,4,4,5,99,9801)
    assertEquals(expected, Computer(ArrayBlockingQueue<Long>(1), ArrayBlockingQueue<Long>(1)).execute(program))
}

fun testExample3() {
    val program : MutableList<Long> = mutableListOf(1,1,1,4,99,5,6,0,99)
    val expected : MutableList<Long> = mutableListOf(30,1,1,4,2,5,6,0,99)
    assertEquals(expected, Computer(ArrayBlockingQueue<Long>(1), ArrayBlockingQueue<Long>(1)).execute(program))
}

fun testExample4() {
    val program : MutableList<Long> = mutableListOf(1002,4,3,4,33)
    val expected : MutableList<Long> = mutableListOf(1002,4,3,4,99)
    assertEquals(expected, Computer(ArrayBlockingQueue<Long>(1), ArrayBlockingQueue<Long>(1)).execute(program))
}

fun testExample5() {
    // Output 1 if input is 8, else 0
    val program = mutableListOf<Long>(3,9,8,9,10,9,4,9,99,-1,8)
    val output = ArrayBlockingQueue<Long>(1)
    var computer = Computer(8.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1, output.take())

    computer = Computer(7.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(0, output.take())
}

fun testExample6() {
    // Output 1 if input is less than 8, else 0
    val program = mutableListOf<Long>(3,9,7,9,10,9,4,9,99,-1,8)
    val output = ArrayBlockingQueue<Long>(1)
    var computer =  Computer(8.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(0, output.take())

    computer = Computer(7.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1, output.take())
}

fun testExample7() {
    // Output 1 if input is equal 8, else 0
    val program = mutableListOf<Long>(3,3,1108,-1,8,3,4,3,99)
    val output = ArrayBlockingQueue<Long>(1)
    var computer =  Computer(8.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1, output.take())

    computer = Computer(7.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(0, output.take())
}

fun testExample8() {
    // Output 1 if input is less than 8, else 0
    val program = mutableListOf<Long>(3,3,1107,-1,8,3,4,3,99)
    val output = ArrayBlockingQueue<Long>(1)
    var computer =  Computer(8.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(0, output.take())

    computer = Computer(7.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1, output.take())
}

fun testExample9() {
    // Output 0 if input is 0, else 1
    var program = mutableListOf<Long>(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9)
    val output = ArrayBlockingQueue<Long>(1)
    var computer = Computer(1.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1, output.take())

    program = mutableListOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9)
    computer = Computer(0.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(0, output.take())
}

fun testExample10() {
    // Output 0 if input is 0, else 1
    var program = mutableListOf<Long>(3,3,1105,-1,9,1101,0,0,12,4,12,99,1)
    val output = ArrayBlockingQueue<Long>(1)
    var computer = Computer(1.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1, output.take())

    computer = Computer(0.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(0, output.take())
}

fun testExample11() {
    //  The program will then output 999 if the input value is below 8,
    //  output 1000 if the input value is equal to 8,
    //  or output 1001 if the input value is greater than 8.
    val program = mutableListOf<Long>(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
            1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
            999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
    val output = ArrayBlockingQueue<Long>(1)
    var computer = Computer(7.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(999, output.take())

    computer = Computer(8.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1000, output.take())

    computer = Computer(9.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(1001, output.take())
}

fun testParameterMode() {
    var memory = Memory(mutableListOf(761002, 1, 2, 3, 4, 5, 7, 8, 9, 10))
    assertEquals(0, memory.getParameterMode(0, 1))
    assertEquals(1, memory.getParameterMode(0, 2))
    assertEquals(6, memory.getParameterMode(0, 3))
    assertEquals(7, memory.getParameterMode(0, 4))
    assertEquals(0, memory.getParameterMode(0, 5))
}

fun testIOInstruction(inputValue : Int) {
    var program = mutableListOf<Long>(3,0,4,0,99)
    val output = ArrayBlockingQueue<Long>(1)
    val computer = Computer(inputValue.toBlockingQueue(1), output)
    computer.execute(program)
    assertEquals(inputValue.toLong(), output.take())
}

fun testExample12() {
    val program = mutableListOf<Long>(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)
    val expected = program.toMutableList()
    val output = ArrayBlockingQueue<Long>(2*program.size)
    Computer(ArrayBlockingQueue<Long>(1), output).execute(program)
    assertEquals(expected, output.toMutableList())
}

fun testExample13() {
    val program = mutableListOf<Long>(1102,34915192,34915192,7,4,7,99,0)
    val output = ArrayBlockingQueue<Long>(2*program.size)
    Computer(ArrayBlockingQueue<Long>(1), output).execute(program)
    assertEquals(16, output.take().toString().length)
}

fun testExample14() {
    val program = mutableListOf<Long>(104,1125899906842624,99)
    val expected = 1125899906842624
    val output = ArrayBlockingQueue<Long>(2*program.size)
    Computer(ArrayBlockingQueue<Long>(1), output).execute(program)
    assertEquals(expected, output.take())
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
    testExample12()
    testExample13()
    testExample14()
}


fun loadProgram(fileName : String) : List<Long> {
    return File(fileName).readText().trim().split(",").map { it.toLong() }.toList()
}

fun part01() {
    var program = loadProgram("day_09/input.txt").toMutableList()
    val output = ArrayBlockingQueue<Long>(500)
    Computer(1.toBlockingQueue(1), output).execute(program)
    //println("[Part 01] Max thruster signal: $signal (for setting=$setting)")
}
fun main() {
   //tests()
   part01()
}