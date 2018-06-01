package ec.com.dovic.aprendiendo.main


/**
 * Created by victor on 27/1/18.
 */
interface MainPresenter {

    fun onResume()
    fun onPause()
    fun inSession()
    fun onInSessionRemove()
}