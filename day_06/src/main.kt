import sun.reflect.generics.tree.Tree
import java.io.File
import java.lang.IllegalStateException
import kotlin.test.assertEquals

fun getParentAndChildId(value: String): Pair<String, String> {
    var parts = value.split(")")
    if (parts.size == 2)
        return Pair(parts[0], parts[1])

    throw IllegalStateException("Give value ($value) do not contain exactly one orbit declaration!")
}

fun createTree(orbits : List<String>) : TreeNode<String> {
    var nodes = mutableMapOf<String, TreeNode<String>>()

    for (orbit in orbits) {
        var (parentId, childId) = getParentAndChildId(orbit)

        var child = nodes[childId]
        var parent = nodes[parentId]

        if (child == null) child = TreeNode(childId)
        if (parent == null) parent = TreeNode(parentId)

        child.parent = parent
        parent.addChild(child)

        nodes[childId] = child
        nodes[parentId] = parent
    }

    var root = nodes["COM"]
    if (root != null)
        return root
    throw IllegalStateException("Given orbits (${orbits}) do not form a tree!")
}

fun countOrbits(node: TreeNode<String>, level: Int): Int {
    return level + node.children.map { countOrbits(it, level + 1) }.sum()
}

fun example1() {
    val orbits = listOf("COM)B", "B)C", "C)D", "D)E", "E)F", "B)G", "G)H", "D)I", "E)J", "J)K", "K)L")
    assertEquals(42, countOrbits(createTree(orbits), 0))
}

fun loadInput(fileName : String) : List<String> {
    return File(fileName).readLines().toList()
}

fun part1() {
    println("Part 01: Total number of orbits: ${countOrbits(createTree(loadInput("day_06/input.txt")), 0)}")
}

fun main() {
    example1()
    part1()
}