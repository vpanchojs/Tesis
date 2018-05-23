package ec.com.dovic.aprendiendo.updateQuestionnaire

import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.updateQuestionnaire.events.UpdateQuestionaireEvents

/**
 * Created by victor on 5/2/18.
 */
interface UpdateQuestionairePresenter {

    fun onResume()

    fun onPause()

    fun onUploadQuestionaire(questionaire: Questionaire)

    fun onGetQuestionaire(any: Any)

    fun onEventNewQuestionaireThread(event: UpdateQuestionaireEvents)
}