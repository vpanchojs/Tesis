package ec.com.dovic.aprendiendo.repository.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.questionnaireResume.ui.QuestionnaireResumeActivity
import ec.com.dovic.aprendiendo.repository.QuestionnaireRepositoryPresenter
import ec.com.dovic.aprendiendo.repository.adapter.QuestionnaireRepositoryAdapter
import ec.com.dovic.aprendiendo.util.BaseActivitys
import kotlinx.android.synthetic.main.activity_questionnaire_repository.view.*
import javax.inject.Inject

class QuestionnaireRepositoryFragment : Fragment(), QuestionnaireRepositoryView, ec.com.dovic.aprendiendo.repository.adapter.onQuestionnaireAdapterListener {

    override fun navigationToDetailQuestionnarie(questionaire: Any) {
        startActivity(Intent(context, QuestionnaireResumeActivity::class.java).putExtra(QuestionnaireResumeActivity.PARAM_QUESTIONNAIRE, questionaire as Questionaire))
    }

    var questionnaries = ArrayList<Questionaire>()
    lateinit var application: MyApplication
    lateinit var progresbar: ProgressBar
    lateinit var tv_none_questionnaires: TextView
    lateinit var adapter: QuestionnaireRepositoryAdapter


    @Inject
    lateinit var presenter: QuestionnaireRepositoryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = QuestionnaireRepositoryAdapter(questionnaries, this)
        setupInjection()
    }

    private fun setupInjection() {
        application = activity!!.getApplication() as MyApplication
        application.getQuestionnaireRepoComponent(this).inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onSuscribe()
        presenter.onGetQuestionnaireRepo()
    }

    override fun onStop() {
        super.onStop()
        presenter.onUnSuscribe()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_questionnaire_repository, container, false)
        view.rv_questionnaire.layoutManager = LinearLayoutManager(context)
        view.rv_questionnaire.adapter = adapter
        progresbar = view.progressbar
        tv_none_questionnaires = view.tv_none_questionnaires
        return view
    }

    override fun showProgress(show: Boolean) {
        if (show) progresbar.visibility = View.VISIBLE else progresbar.visibility = View.GONE
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(context!!, message, Toast.LENGTH_SHORT)
    }

    override fun setQuestionnaries(questionaire: List<Questionaire>) {
        questionnaries!!.clear()
        adapter.data.addAll(questionaire)
        adapter.notifyDataSetChanged()
    }

    override fun none_results(show: Boolean) {
        if (show) tv_none_questionnaires.visibility = View.VISIBLE else tv_none_questionnaires.visibility = View.GONE
    }

    companion object {
        fun newInstance(): QuestionnaireRepositoryFragment {
            val fragment = QuestionnaireRepositoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment

        }
    }
}