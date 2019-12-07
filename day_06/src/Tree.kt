
class TreeNode<T>(value: T) {
    var children : MutableList<TreeNode<T>> = mutableListOf()
    var parent : TreeNode<T>? = null
    val value: T = value
}

fun <T> TreeNode<T>.addChild(node: TreeNode<T>) {
    children.add(node)
}