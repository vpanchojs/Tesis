package ec.edu.unl.blockstudy.repository.ui

import ec.edu.unl.blockstudy.entities.Questionaire

interface QuestionnaireRepositoryView {
    fun showProgress(show: Boolean)
    fun showMessagge(message: Any)
    fun setQuestionnaries(questionaire: List<Questionaire>)
    fun none_results(show: Boolean)

}
