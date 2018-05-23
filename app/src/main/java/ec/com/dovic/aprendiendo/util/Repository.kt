package ec.com.dovic.aprendiendo.util

/**
 * Created by victor on 6/3/18.
 */
interface Repository {
    fun postEvent(type: Int, any: Any)
}