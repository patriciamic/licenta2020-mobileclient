package com.voice.licentaclientkotlin.entities.dataclasses.conversation

import com.voice.licentaclientkotlin.entities.Goal

data class ConversationData(val conversation: Conversation, val root: Goal, val tree: List<ConversationTreeNode>)