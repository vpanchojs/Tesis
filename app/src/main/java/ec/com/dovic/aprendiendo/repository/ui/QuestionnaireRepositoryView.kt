package ec.com.dovic.aprendiendo.repository.ui

import ec.com.dovic.aprendiendo.entities.Questionaire

interface QuestionnaireRepositoryView {
    fun showProgress(show: Boolean)
    fun showMessagge(message: Any)
    fun setQuestionnaries(questionaire: List<Questionaire>)
    fun none_results(show: Boolean)
    fun setRecomendations(questionnaire_list: List<Questionaire>)
    fun none_recommendations(visible: Int)

}
