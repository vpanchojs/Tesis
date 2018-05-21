package ec.edu.unl.blockstudy.myquestionnaires.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.database.QuestionnaireBd
import ec.edu.unl.blockstudy.myquestionnaires.MyQuestionairePresenter
import ec.edu.unl.blockstudy.myquestionnaires.adapter.QuestionnaireAdapter
import ec.edu.unl.blockstudy.myquestionnaires.adapter.onQuestionnaireAdapterListener
import ec.edu.unl.blockstudy.myrepository.ui.MyRepositoryActivity
import ec.edu.unl.blockstudy.questionsComplete.ui.QuestionsCompleteActivity
import ec.edu.unl.blockstudy.util.BaseActivitys
import kotlinx.android.synthetic.main.fragment_my_questionnaires.*
import javax.inject.Inject

class MyQuestionnairesFragment : Fragment(), View.OnClickListener, MyQuestionnariesView, onQuestionnaireAdapterListener {
    override fun navigationManageQuestionnaire(questionaire: QuestionnaireBd) {
        //startActivity(Intent(context, QuestionaireActivity::class.java).putExtra(QuestionaireActivity.QUESTIONNAIRE, questionaire))
    }

    var questionnaries = ArrayList<QuestionnaireBd>()
    lateinit var application: MyApplication
    lateinit var adapter: QuestionnaireAdapter


    @Inject
    lateinit var presenter: MyQuestionairePresenter

    companion object {
        fun newInstance(): MyQuestionnairesFragment {
            val fragment = MyQuestionnairesFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = QuestionnaireAdapter(questionnaries, this)
        setupInjection()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_questionnaires, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_my_repository.setOnClickListener(this)
        rv_questionnaire.layoutManager = LinearLayoutManager(context)
        rv_questionnaire.adapter = adapter
    }

    private fun setupInjection() {
        application = activity!!.getApplication() as MyApplication
        application.getMyQuestionnarieComponent(this).inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onResume()
        presenter.onGetMyQuestionnaires()
    }

    override fun onStop() {
        super.onStop()
        presenter.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_my_repository -> {
                startActivity(Intent(activity, MyRepositoryActivity::class.java))

            }
        }
    }

    override fun hideProgressDialog() {
        BaseActivitys.hideProgressDialog()
    }

    override fun showProgressDialog(message: Any) {
        BaseActivitys.showProgressDialog(context!!, message)
    }

    override fun showProgress(show: Boolean) {
        if (show) progressbar.visibility = View.VISIBLE else progressbar.visibility = View.GONE
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(context!!, message, Toast.LENGTH_SHORT)
    }

    override fun setQuestionnaries(questionaire: List<QuestionnaireBd>) {
        questionnaries.clear()
        adapter.data.addAll(questionaire)
        adapter.notifyDataSetChanged()
    }

    override fun none_results(show: Boolean) {
        questionnaries.clear()
        if (show) tv_none_questionnaires.visibility = View.VISIBLE else tv_none_questionnaires.visibility = View.GONE
    }

    override fun navigationToDetailQuestionnarie(any: Any) {
        startActivity(Intent(context, QuestionsCompleteActivity::class.java).putExtra(QuestionsCompleteActivity.QUESTIONNAIRE_PARAM, any as QuestionnaireBd))
    }

    fun onCreateQuestionnaire(description: String, title: String) {
        presenter.onCreateQuestionaire(title, description)
    }

    override fun hideDialogNewQuestionnaire() {

    }

    override fun showButtonCreateQuestionnaire() {

    }
}
