package ec.com.dovic.aprendiendo.myquestionnaires

import ec.com.dovic.aprendiendo.myquestionnaires.events.MyQuestionaireEvents

/**
 * Created by victor on 24/2/18.
 */
interface MyQuestionairePresenter {

    fun onResume()

    fun onPause()

    fun onGetMyQuestionnaires()

    fun onCreateQuestionaire(title: String, description: String)

    fun onEventThread(event: MyQuestionaireEvents)
}