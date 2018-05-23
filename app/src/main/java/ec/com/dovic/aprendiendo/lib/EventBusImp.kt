package ec.com.dovic.aprendiendo.lib

import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import org.greenrobot.eventbus.EventBus

class EventBusImp(var eventBus: EventBus) : EventBusInterface {

    override fun register(subscriber: Any?) {
        eventBus.register(subscriber)
    }

    override fun unregister(subscriber: Any?) {
        eventBus.unregister(subscriber)
    }

    override fun post(event: Any?) {
        eventBus.post(event)
    }
}