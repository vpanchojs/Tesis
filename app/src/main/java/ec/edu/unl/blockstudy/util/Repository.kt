package ec.edu.unl.blockstudy.util

/**
 * Created by victor on 6/3/18.
 */
interface Repository {
    fun postEvent(type: Int, any: Any)
}