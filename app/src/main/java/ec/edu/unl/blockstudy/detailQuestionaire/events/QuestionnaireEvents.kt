package ec.edu.unl.blockstudy.detailQuestionaire.events

/**
 * Created by victor on 25/2/18.
 */
class QuestionnaireEvents(var type: Int, var any: Any) {

    companion object {
        val ON_GET_QUESTIONS_SUCCESS = 0
        val ON_GET_QUESTIONS_ERROR = 1
        val ON_UPDATE_BASIC_QUESTIONNAIRE = 2
        val ON_DELETE_QUESTIONNAIRE_SUCCESS = 3
    }

}