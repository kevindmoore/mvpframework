package com.mastertechsoftware.mvpframework

import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Manager for handling Events. Currently uses EventBus for actual work
 * Create multiple EventBus instances for each channel
 */
open class EventManager {
    val defaultChannel = Channel("Default")
    val channelMap = HashMap<Channel, EventBus>()

    /**
     * Is the subscriber registered
     * @param subscriber
     */
    fun isRegistered(subscriber: Any) : Boolean {
        var bus = channelMap.get(defaultChannel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(defaultChannel, bus)
        }
        return bus.isRegistered(subscriber)
    }

    /**
     * Is the subscriber registered
     * @param channel
     * @param subscriber
     */
    fun isRegistered(channel: Channel, subscriber: Any) : Boolean {
        var bus = channelMap.get(channel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(channel, bus)
        }
        return bus.isRegistered(subscriber)
    }

   /**
     * Register a subscriber to a specific channel
     * @param subscriber
     */
    fun register(subscriber: Any) {
        var bus = channelMap.get(defaultChannel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(defaultChannel, bus)
        }
        bus.register(subscriber)
    }

    /**
     * Register a subscriber to a specific channel
     * @param channel
     * @param subscriber
     */
    fun register(channel: Channel, subscriber: Any) {
        var bus = channelMap.get(channel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(channel, bus)
        }
        bus.register(subscriber)
    }

    /**
     * Unregister a subscriber from the default channel
     * @param subscriber
     */
    fun unregister(subscriber: Any) {
        var bus = channelMap.get(defaultChannel)
        if (bus == null) {
            throw IllegalArgumentException("Bus not found for ${defaultChannel}")
        }
        bus.unregister(subscriber)
    }


    /**
     * Unregister a subscriber from a specific channel
     * @param channel
     * @param subscriber
     */
    fun unregister(channel: Channel, subscriber: Any) {
        var bus = channelMap.get(channel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(channel, bus)
        }
        bus.unregister(subscriber)
    }

    /**
     * Post an event to a the default channel
     * @param event
     */
    fun post(event: Any) {
        var bus = channelMap.get(defaultChannel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(defaultChannel, bus)
        }
        bus.post(event)
    }


    /**
     * Post an event to a specific channel
     * @param channel
     * @param event
     */
    fun post(channel: Channel, event: Any) {
        var bus = channelMap.get(channel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(channel, bus)
        }
        bus.post(event)
    }

    /**
     * Post a sticky event to the default channel
     * @param event
     */
    fun postSticky(event: Any) {
        var bus = channelMap.get(defaultChannel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(defaultChannel, bus)
        }
        bus.postSticky(event)
    }

    /**
     * Post a sticky event to a specific channel
     * @param channel
     * @param event
     */
    fun postSticky(channel: Channel, event: Any) {
        var bus = channelMap.get(channel)
        if (bus == null) {
            bus = EventBus()
            channelMap.put(channel, bus)
        }
        bus.postSticky(event)
    }
}

data class Channel(val channelName: String) {

}