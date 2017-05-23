package com.mastertechsoftware.mvpframework

import com.mastertechsoftware.logging.Logger
import java.util.*

/**
 * Implementation of ScreenManager. Can be replaced
 */
open class ScreenManagerImpl : ScreenManager {
    protected var currentLogic : ScreenLogic? = null
    protected val stack = ArrayDeque<ScreenLogic>()
    protected var showDebugging = true

    override fun stackSize(): Int {
        return stack.size
    }

    override fun currentLogic(): ScreenLogic? {
        return currentLogic
    }

    override fun pop(): ScreenLogic? {
        if (showDebugging) {
            Logger.debug("pop")
        }
        currentLogic?.pause()
        val topLogic = popStack()
        currentLogic?.resume()
        return topLogic
    }

    private fun popStack() : ScreenLogic?{
        if (showDebugging) {
            Logger.debug("popStack")
        }
        if (stack.isEmpty()) {
            return null
        }
        val topLogic = stack.pop()
        if (!stack.isEmpty()) {
            currentLogic = stack.peek()
        } else {
            currentLogic = null
        }
        if (showDebugging) {
            Logger.debug("popStack: ${currentLogic}")
        }
        return topLogic
    }

    override fun push(screenLogic: ScreenLogic) {
        if (showDebugging) {
            Logger.debug("push ${screenLogic}")
        }
        currentLogic?.pause()
        screenLogic.setScreenManager(this)
        stack.push(screenLogic)
        currentLogic = screenLogic
        if (!screenLogic.started()) {
            screenLogic.start()
        }
        screenLogic.resume()
    }

    override fun replace(screenLogic: ScreenLogic) {
        if (showDebugging) {
            Logger.debug("replace ${screenLogic}")
        }
        screenLogic.setScreenManager(this)
        currentLogic?.pause()
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