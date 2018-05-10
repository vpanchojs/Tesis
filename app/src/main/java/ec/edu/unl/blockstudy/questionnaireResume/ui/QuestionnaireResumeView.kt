package ec.edu.unl.blockstudy.questionnaireResume.ui

import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.Raiting
import ec.edu.unl.blockstudy.entities.User

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
}