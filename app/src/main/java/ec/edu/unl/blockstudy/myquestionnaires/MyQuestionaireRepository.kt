package ec.edu.unl.blockstudy.myquestionnaires

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 24/2/18.
 */
interface MyQuestionaireRepository {
    fun onGetMyQuestionnaires()
    fun onCreateQuestionaire(questionaire: Questionaire)
}