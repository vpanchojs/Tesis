package ec.com.dovic.aprendiendo.newQuestion

import ec.com.dovic.aprendiendo.entities.Question

/**
 * Created by victor on 26/2/18.
 */
interface QuestionRepository {
    fun onCreateQuestion(question: Question, idQuestionnaire: String)
    fun onGetDataQuestion(idQuestion: Any, idQuesitonnaire: Any)
    fun onUpdateQuestion(question: Question)
    fun onDeteleQuestion(idQuestion: String, idQuestionnaire: String)
}