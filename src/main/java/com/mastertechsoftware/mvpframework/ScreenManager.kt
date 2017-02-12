package com.mastertechsoftware.mvpframework

/**
 * Manager that handles going from screen to screen using
 * ScreenLogic classes
 */
interface ScreenManager {
    fun start()
    fun stop()
    fun currentLogic() : ScreenLogic?
    fun replace(screenLogic: ScreenLogic)
    fun push(screenLogic: ScreenLogic)
    fun pop() : ScreenLogic?
    fun stackSize() : Int
}