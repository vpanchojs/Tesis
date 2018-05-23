package ec.com.dovic.aprendiendo.domain.listeners

interface OnCallbackApis<T> {
    fun onSuccess(response: T)
    fun onError(error: Any?)
}