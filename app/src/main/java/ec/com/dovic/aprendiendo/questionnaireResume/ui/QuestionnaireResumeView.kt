package ec.com.dovic.aprendiendo.questionnaireResume.ui

import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.entities.Score
import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by victor on 27/3/18.
 */
interface QuestionnaireResumeView {
    fun showMessagge(message: Any)
    fun showProgress(view: Int)
    fun none_results(show: Boolean)
    fun navigationBack()
    fun setQuestions(questionList: List<Question>)
    fun setUser(user: User)
    fun updateRating(rating: Score)
    fun setRatings(ratingList: List<Score>)
    fun showButtonRaiting(visible: Int)
    fun dowloadQuestionnaire()
    fun confirmDownloadQuestionnaire()
    fun setDownload(b: Boolean)
    fun setDataQuestionnaire(questionaire: Questionaire)
}