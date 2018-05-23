package ec.com.dovic.aprendiendo.menu

import ec.com.dovic.aprendiendo.menu.events.MenusEvents

/**
 * Created by victor on 27/1/18.
 */
interface MenusPresenter {

    fun onResume()

    fun onPause()

    fun onSingOut()

    fun getMyProfile()

    fun onUpdatePassword(password: String, passwordOld: String)

    fun onEventMenuThread(event: MenusEvents)
}