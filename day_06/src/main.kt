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

fun findPath(nodeId : String, node: TreeNode<String>): List<String> {
    if (node.value == nodeId)
        return listOf(node.value)

    for (child in node.children) {
        val subPath = findPath(nodeId, child)
        if (subPath.isNotEmpty())
            return listOf(node.value) + subPath
    }

    return listOf()
}

fun findMinNumTransfers(source: String, destination: String, tree: TreeNode<String>): Int {
    val pathToSource = findPath(source, tree).toMutableList()
    val pathToDestination = findPath(destination, tree).toMutableList()

    if (pathToDestination.isEmpty()) throw IllegalStateException("Destination ($destination) not found in tree")
    if (pathToSource.isEmpty()) throw IllegalStateException("Source ($source) not found in tree")

    val commonPath = pathToDestination.intersect(pathToSource)

    // Remove common path
    pathToSource.removeAll(commonPath)
    pathToDestination.removeAll(commonPath)

    // Number of transfer is equal to the combined path length, excluding the source and destination elements them self
    return pathToDestination.size - 1 + pathToSource.size -1
}

fun example2() {
    val orbits = listOf("COM)B", "B)C", "C)D", "D)E", "E)F", "B)G", "G)H", "D)I", "E)J",
            "J)K", "K)L", "K)YOU", "I)SAN")
    val tree = createTree(orbits)

    assertEquals(4, findMinNumTransfers("YOU", "SAN", tree))
}

fun part2() {
    println("Required transfers from YOU to SAN: ${findMinNumTransfers("YOU", "SAN",
            createTree(loadInput("day_06/input.txt")))}")
}

fun main() {
    example1()
    part1()
    example2()
    part2()
}