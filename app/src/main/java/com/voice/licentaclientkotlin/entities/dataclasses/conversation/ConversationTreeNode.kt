package com.voice.licentaclientkotlin.entities.dataclasses.conversation

import com.voice.licentaclientkotlin.entities.Goal

data class ConversationTreeNode(val child: Goal, val parent: Goal)