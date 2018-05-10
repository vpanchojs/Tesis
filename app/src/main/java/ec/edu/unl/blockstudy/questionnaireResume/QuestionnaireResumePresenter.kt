package ec.edu.unl.blockstudy.questionnaireResume

import ec.edu.unl.blockstudy.questionnaireResume.events.QuestionnaireResumeEvents
import ec.edu.unl.blockstudy.util.Presenter

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