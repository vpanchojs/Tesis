package ec.edu.unl.blockstudy.questionnaireResume

/**
 * Created by victor on 27/3/18.
 */
interface QuestionnaireResumeInteractor {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetRaitingsAll(idQuestionnaire: Any)
    fun setRaiting(idQuestionnaire: Any, raiting: Double, message: String)
    fun onGetUser(idUser: Any)
    fun getQuestionnaire(idQuestionnaire: String)
}