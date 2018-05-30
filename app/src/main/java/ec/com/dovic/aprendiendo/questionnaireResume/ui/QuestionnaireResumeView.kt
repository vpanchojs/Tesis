package ec.com.dovic.aprendiendo.questionnaireResume.ui

import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Raiting
import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by victor on 27/3/18.
 */
interface QuestionnaireResumeView {
    fun showMessagge(message: Any)
    fun showProgress(show: Boolean)
    fun none_results(show: Boolean)
    fun navigationBack()
    fun setQuestions(questionList: List<Question>)
    fun setUser(user: User)
    fun updateRating(rating: Double)
    fun setRatings(ratingList: List<Raiting>)
    fun showButtonRaiting(visible: Int)
    fun dowloadQuestionnaire()
    fun confirmDownloadQuestionnaire()
}