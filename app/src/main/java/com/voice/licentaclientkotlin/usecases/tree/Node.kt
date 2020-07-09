package com.voice.licentaclientkotlin.usecases.tree

import com.voice.licentaclientkotlin.entities.Goal

class Node {

    private var goal: Goal? = null
    private var parent: Node? = null
    private var children: MutableList<Node?>? = null

    constructor(goal: Goal?) {
        this.goal = goal
        children = ArrayList()
    }

    fun getGoal(): Goal? {
        return goal
    }

    fun setGoal(goal: Goal?) {
        this.goal = goal
    }

    fun getParent(): Node? {
        return parent
    }

    fun setParent(parent: Node?) {
        this.parent = parent
    }

    fun getChildren(): List<Node?>? {
        return children
    }

    fun setChildren(children: MutableList<Node?>?) {
        this.children = children
    }

    fun addChild(child: Node) {
        child.setParent(this)
        children!!.add(child)
    }

    fun addChild(data: Goal?) {
        val newChild = Node(data)
        this.addChild(newChild)
    }

    fun addChildren(children: List<Node>) {
        for (node in children) {
            node.setParent(this)
        }
        this.children!!.addAll(children)
    }

}