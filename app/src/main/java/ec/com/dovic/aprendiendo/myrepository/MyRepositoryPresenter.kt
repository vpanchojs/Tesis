package ec.com.dovic.aprendiendo.myrepository

import ec.com.dovic.aprendiendo.myrepository.events.MyRepositoryEvents

/**
 * Created by victor on 24/2/18.
 */
interface MyRepositoryPresenter {

    fun onResume()

    fun onPause()

    fun onGetmyrepository()

    fun onCreateQuestionaire(title: String, description: String)

    fun onEventThread(event: MyRepositoryEvents)
}