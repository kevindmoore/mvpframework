package com.mastertechsoftware.mvpframework

import java.util.*

/**
 * Implementation of ScreenManager. Can be replaced
 */
open class ScreenManagerImpl : ScreenManager {
    var currentLogic : ScreenLogic? = null
    val stack = ArrayDeque<ScreenLogic>()

    override fun stackSize(): Int {
        return stack.size
    }

    override fun currentLogic(): ScreenLogic? {
        return currentLogic
    }

    override fun pop(): ScreenLogic? {
        currentLogic?.stop()
        val topLogic = popStack()
        currentLogic?.resume()
        return topLogic
    }

    private fun popStack() : ScreenLogic?{
        if (stack.isEmpty()) {
            return null
        }
        val topLogic = stack.pop()
        if (!stack.isEmpty()) {
            currentLogic = stack.peek()
        } else {
            currentLogic = null
        }
        return topLogic
    }

    override fun push(screenLogic: ScreenLogic) {
        currentLogic?.pause()
        screenLogic.setScreenManager(this)
        stack.push(screenLogic)
        currentLogic = screenLogic
        screenLogic.start()
    }

    override fun replace(screenLogic: ScreenLogic) {
        screenLogic.setScreenManager(this)
        currentLogic?.stop()
        popStack()
        currentLogic = screenLogic
        stack.push(screenLogic)
        screenLogic.start()
    }

    override fun start() {
    }

    override fun stop() {
    }
}