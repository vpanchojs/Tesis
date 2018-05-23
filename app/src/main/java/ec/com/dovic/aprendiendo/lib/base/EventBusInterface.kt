package ec.com.dovic.aprendiendo.lib.base

interface EventBusInterface {

    fun register(subscriber: Any?)
    fun unregister(subscriber: Any?)
    fun post(subscriber: Any?)

}