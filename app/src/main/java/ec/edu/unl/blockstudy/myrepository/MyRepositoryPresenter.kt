package ec.edu.unl.blockstudy.myrepository

import ec.edu.unl.blockstudy.myrepository.events.MyRepositoryEvents

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