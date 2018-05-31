package ec.com.dovic.aprendiendo.detailQuestionaire

import android.util.Log
import android.view.View
import ec.com.dovic.aprendiendo.detailQuestionaire.events.QuestionnaireEvents
import ec.com.dovic.aprendiendo.detailQuestionaire.ui.QuestionnaireView
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 25/2/18.
 */
class QuestionnariesPresenterImp(var eventBus: EventBusInterface, var view: QuestionnaireView, var interactor: QuestionnariesInteractor) : QuestionnariesPresenter {

    override fun onResume() {
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun onGetDataQuestionnaire(any: Any) {
        view.showProgress(View.VISIBLE)
        interactor.onGetDataQuestionnaire(any)
    }

    override fun onSaveQuestion(idQuestionarie: Long, question: Question) {
        interactor.onSaveQuestion(idQuestionarie, question)
    }

    override fun updateBasicQuestionnaire(questionaire: Questionaire) {
        view.showProgressDialog("Actualizando Información Básica del Cuestionario")
        interactor.updateBasicQuestionnaire(questionaire)
    }

    override fun onDeleteQuestionnnaire(idQuestionaire: Any) {
        view.showProgressDialog("Eliminando Cuestionario")
        interactor.onDeleteQuestionnnaire(idQuestionaire)
    }

    override fun isExistQuestionnnaireLocal(idCloud: String) {
        interactor.isExistQuestionnnaireLocal(idCloud)
    }

    @Subscribe
    override fun onEventThread(event: QuestionnaireEvents) {
        when (event.type) {
            QuestionnaireEvents.ON_GET_QUESTIONS_SUCCESS -> {
                view.showProgress(View.GONE)

                var questionList = event.any as List<Question>
                if (questionList.size > 0)
                    view.setQuestions(questionList)
                else
                    view.none_results(true)

            }
            QuestionnaireEvents.ON_GET_QUESTIONS_ERROR -> {
                view.showProgress(View.GONE)

            }
            QuestionnaireEvents.ON_UPDATE_BASIC_QUESTIONNAIRE -> {
                view.hideProgressDialog()
                view.showMessagge("Información Básica Actualizada")
            }
            QuestionnaireEvents.ON_DELETE_QUESTIONNAIRE_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("Cuestionario Eliminado")
                view.navigationBack()
            }
            QuestionnaireEvents.ON_IS_EXIST_QUESTIONNNAIRE_LOCAL -> {
                Log.e("PRE", "SI HUBO RESPUESTA ${event.any as Boolean}")
                if (event.any as Boolean) {
                    view.confirmDownloadQuestionnaire()
                } else {
                    view.dowloadQuestionnaire()
                }
            }
        }
    }

}