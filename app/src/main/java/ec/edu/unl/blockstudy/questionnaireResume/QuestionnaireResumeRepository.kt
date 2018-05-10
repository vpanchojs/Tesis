package ec.edu.unl.blockstudy.questionnaireResume

import ec.edu.unl.blockstudy.entities.Raiting
import ec.edu.unl.blockstudy.util.Repository

/**
 * Created by victor on 27/3/18.
 */
interface QuestionnaireResumeRepository : Repository {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetRaitingsAll(idQuestionnaire: Any)
    fun setRaiting(raiting: Raiting)
    fun onGetUser(idUser: Any)
    fun getQuestionnaire(idQuestionnaire: String)
}