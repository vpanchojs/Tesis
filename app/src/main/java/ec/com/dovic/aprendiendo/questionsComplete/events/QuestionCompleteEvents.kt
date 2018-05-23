package ec.com.dovic.aprendiendo.questionsComplete.events

/**
 * Created by victor on 26/2/18.
 */
class QuestionCompleteEvents(var type: Int, var any: Any) {

    companion object {
        val ON_GET_QUESTIONS_SUCCESS = 2
        val ON_GET_QUESTIONS_ERROR = 3
        val ON_GET_ANSWERS_SUCCESS = 4
        val ON_DELETE_SUCCESS = 5
        val ON_DELETE_ERROR = 6

    }
}