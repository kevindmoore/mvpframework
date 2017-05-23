package com.mastertechsoftware.mvpframework

/**
 * Logic for a specific screen. Load the screen, handle action/events
 */
interface ScreenLogic {
    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun onReturn()
    fun started() : Boolean
    fun setScreenManager(screenManager: ScreenManager)
}