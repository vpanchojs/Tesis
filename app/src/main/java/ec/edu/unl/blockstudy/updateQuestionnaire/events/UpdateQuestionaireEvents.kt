package ec.edu.unl.blockstudy.updateQuestionnaire.events

/**
 * Created by victor on 5/2/18.
 */
class UpdateQuestionaireEvents(var type: Int, var any: Any) {

    companion object {
        val ON_POST_QUESTIONAIRE_SUCCESS = 0
        val ON_POST_QUESTIONAIRE_ERROR = 1
        val ON_GET_QUESTIONNAIRE_SUCCESS = 2
    }

}