package ec.edu.unl.blockstudy.domain.listeners

/**
 * Created by victor on 15/1/18.
 */
interface onDomainApiActionListener {
    fun onSuccess(response: Any?)
    fun onError(error: Any?)
}