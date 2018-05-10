package ec.edu.unl.blockstudy.newQuestionnaire

import ec.edu.unl.blockstudy.entities.Questionaire

/**
 * Created by victor on 5/2/18.
 */
interface NewQuestionaireInteractor {
    fun onUploadQuestionaire(questionaire: Questionaire)
}