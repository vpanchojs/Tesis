package ec.com.dovic.aprendiendo.questionnaireResume

import ec.com.dovic.aprendiendo.entities.Score
import ec.com.dovic.aprendiendo.util.Repository

/**
 * Created by victor on 27/3/18.
 */
interface QuestionnaireResumeRepository : Repository {
    fun onGetQuestionAll(idQuestionnaire: Any)
    fun onGetRaitingsAll(idQuestionnaire: Any)
    fun setRaiting(raiting: Score, update: Boolean, oldRaiting: Double)
    fun onGetUser(idUser: Any)
    fun getQuestionnaire(idQuestionnaire: String)
    fun isDownloaded(idQuestionnaire: String)
    fun isExistQuestionnnaireLocal(idCloud: String)
}