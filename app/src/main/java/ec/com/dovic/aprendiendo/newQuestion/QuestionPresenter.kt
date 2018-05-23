package ec.com.dovic.aprendiendo.newQuestion

import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.newQuestion.events.QuestionEvents

/**
 * Created by victor on 26/2/18.
 */
interface QuestionPresenter {

    fun onResume()

    fun onPause()

    fun onEventThread(event: QuestionEvents)

    fun onCreateQuestion(url: String, anserws: ArrayList<Answer>, statements: String, idQuesitonnaire: String)

    fun onGetDataQuestion(idQuestion: Any, idQuesitonnaire: Any)

    fun updateQuestion(s: String, answerList: ArrayList<Answer>, toString: String, idQuestion: Long, idQuestionnaire: Long)

    fun updateQuestion(question: Question)

    fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String)

    fun updateQuestion(idQuestion: String, statament: String, photo_url: String, answerList: java.util.ArrayList<Answer>?, idQuestionnaire: String)


}