package ec.edu.unl.blockstudy.menu

import ec.edu.unl.blockstudy.menu.events.MenusEvents

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