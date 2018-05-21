package ec.edu.unl.blockstudy.questionsComplete

import android.util.Log
import ec.edu.unl.blockstudy.database.QuestionBd
import ec.edu.unl.blockstudy.database.QuestionnaireBd
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.newQuestion.events.QuestionEvents
import ec.edu.unl.blockstudy.questionsComplete.events.QuestionCompleteEvents
import ec.edu.unl.blockstudy.questionsComplete.ui.QuestionCompleteView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by victor on 5/3/18.
 */
class QuestionCompletePresenterImp(var eventBus: EventBusInterface, var view: QuestionCompleteView, var interactor: QuestionCompleteInteractor) : QuestionCompletePresenter {

    override fun onSuscribe() {
        eventBus.register(this)
    }

    override fun onUnSuscribe() {
        eventBus.unregister(this)
    }

    override fun onGetQuestionAll(idQuestionnaire: Any) {
        interactor.onGetQuestionAll(idQuestionnaire)
    }

    override fun onGetAnswersQuestion(idQuestionnaire: String, idCloud: String) {
        interactor.onGetAnswersQuestion(idQuestionnaire, idCloud)
    }

    override fun deleteQuestionnarie(questionnaireBd: QuestionnaireBd) {
        interactor.deleteQuestionnarie(questionnaireBd)
    }

    @Subscribe
    override fun onEventThread(event: QuestionCompleteEvents) {
        when (event.type) {
            QuestionCompleteEvents.ON_GET_QUESTIONS_SUCCESS -> {
                var questionList = event.any as List<QuestionBd>
                if (questionList.size > 0) {
                    Log.e("pre", "si esta ")
                    view.setQuestions(questionList)
                } else {
                    view.none_results(true)
                }

            }

            QuestionCompleteEvents.ON_GET_ANSWERS_SUCCESS -> {
                var answerList = event.any as List<Answer>
                if (answerList.size > 0)
                    view.setAnswer(answerList)
                else
                    view.none_results(true)
            }

            QuestionCompleteEvents.ON_DELETE_SUCCESS->{
                view.closeActivity()
            }

        }
    }
}