package ec.edu.unl.blockstudy.domain.listeners

interface OnCallbackApis<T> {
    fun onSuccess(response: T)
    fun onError(error: Any?)
}