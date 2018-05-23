package ec.com.dovic.aprendiendo.questionnaireResume

import ec.com.dovic.aprendiendo.questionnaireResume.events.QuestionnaireResumeEvents
import ec.com.dovic.aprendiendo.util.Presenter

/**
 * Created by victor on 27/3/18.
 */
interface QuestionnaireResumePresenter : Presenter {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetRaitingsAll(idQuestionnaire: Any)
    fun onGetUser(idUser: Any)
    fun setRaiting(idQuestionnaire: Any, raiting: Double, message: String)
    fun onEventThread(event: QuestionnaireResumeEvents)
    fun getQuestionnaire(idQuestionnaire: String)

}