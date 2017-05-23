package com.mastertechsoftware.mvpframework

/**
 * Abstract ScreenLogic class. Handles started flag
 * NOTE: Must call super.start() to work
 */
open class AbstractScreenLogic : ScreenLogic {
    protected var started : Boolean = false

    override fun start() {
        started = true
    }

    override fun stop() {
    }

    override fun onReturn() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun started(): Boolean {
        return started
    }

    override fun setScreenManager(screenManager: ScreenManager) {
    }
}