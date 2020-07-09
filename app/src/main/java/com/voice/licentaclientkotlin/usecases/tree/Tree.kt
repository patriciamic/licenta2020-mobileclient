package com.voice.licentaclientkotlin.usecases.tree

import android.util.Log
import com.voice.licentaclientkotlin.entities.dataclasses.conversation.ConversationData

class Tree {
    companion object{
        fun createTree(conversationData: ConversationData): Node {
            val root = Node(conversationData.tree[0].parent)
            root.addChild(conversationData.tree[0].child)
            var currentNode = root;

            for (i in 1 until conversationData.tree.size) {
                val child = conversationData.tree[i].child
                val parent = conversationData.tree[i].parent
                val node = getNodeByIdentity(root, parent.identity)

                if (node != null) {
                    node.addChild(Node(child))
                    currentNode = node
                } else {
                    currentNode.addChild(Node(child))
                }
            }
            return root
        }
        private fun getNodeByIdentity(node: Node, identity: Int): Node? {
            // pre order
            var result: Node? = null
            if (node.getGoal()!!.identity == identity) {
                return node
            }
            node.getChildren()?.let {
                for (n in node.getChildren()!!.indices) {
                    try {
                        result = getNodeByIdentity(node.getChildren()!![n]!!, identity)
                    } catch (e: Exception) {
                        Log.e("Exception for $n", e.message!!)
                    }
                }
            }

            return result
        }
    }
}