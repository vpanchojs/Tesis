package ec.com.dovic.aprendiendo.questionnaireResume.events

/**
 * Created by victor on 26/2/18.
 */
class QuestionnaireResumeEvents(var type: Int, var any: Any) {

    companion object {
        val ON_GET_QUESTIONS_SUCCESS = 0
        val ON_GET_QUESTIONS_ERROR = 1
        val ON_GET_USER_SUCCESS = 2
        val ON_GET_USER_ERROR = 3
        val ON_SET_RATING_SUCCESS = 4
        val ON_SET_RATING_ERROR = 5
        val ON_GET_RATINGS_SUCCESS = 6
        val ON_GET_RATINGS_ERROR = 7
        val ON_GET_IS_DOWNLOADED_SUCCESS = 8
        val ON_IS_EXIST_QUESTIONNNAIRE_LOCAL = 9
        val ON_GET_QUESTIONNAIRE_SUCCESS = 10
        val ON_GET_QUESTIONNAIRE_ERROR = 11
    }
}