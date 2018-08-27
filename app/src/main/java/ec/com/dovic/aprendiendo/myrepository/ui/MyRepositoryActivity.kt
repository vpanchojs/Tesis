package ec.com.dovic.aprendiendo.myrepository.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.detailQuestionaire.ui.QuestionaireActivity
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.myrepository.MyRepositoryPresenter
import ec.com.dovic.aprendiendo.myrepository.adapter.QuestionnaireAdapter
import ec.com.dovic.aprendiendo.myrepository.adapter.onQuestionnaireAdapterListener
import ec.com.dovic.aprendiendo.util.BaseActivitys
import kotlinx.android.synthetic.main.activity_my_questionnaires.*
import javax.inject.Inject

class MyRepositoryActivity : AppCompatActivity(), View.OnClickListener, MyRepositoryView, onQuestionnaireAdapterListener {
    override fun navigationManageQuestionnaire(questionaire: Questionaire) {
        startActivity(Intent(this, QuestionaireActivity::class.java).putExtra(QuestionaireActivity.QUESTIONNAIRE, questionaire))
    }

    var questionnaries = ArrayList<Questionaire>()
    lateinit var application: MyApplication
    lateinit var adapter: QuestionnaireAdapter
    lateinit var newQuestionarieFragment: NewQuestionarieFragment

    @Inject
    lateinit var presenter: MyRepositoryPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_questionnaires)

        adapter = QuestionnaireAdapter(questionnaries, this)

        fab_new_questionnaraire.setOnClickListener(this)
        rv_questionnaire.layoutManager = LinearLayoutManager(this)
        rv_questionnaire.adapter = adapter
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupInjection()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getMyRepositoryComponent(this).inject(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onResume()
        presenter.onGetmyrepository()
    }

    override fun onStop() {
        super.onStop()
        presenter.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_new_questionnaraire -> {
                newQuestionarieFragment = NewQuestionarieFragment.newInstance()
                newQuestionarieFragment.show(supportFragmentManager, "Nuevo Cuestionario")
            }
        }
    }

    override fun hideProgressDialog() {
        BaseActivitys.hideProgressDialog()
    }

    override fun showProgressDialog(message: Any) {
        BaseActivitys.showProgressDialog(this, message)
    }

    override fun showProgress(show: Boolean) {
        if (show) progressbar.visibility = View.VISIBLE else progressbar.visibility = View.GONE
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(this, message, Toast.LENGTH_SHORT)
    }

    override fun setQuestionnaries(questionaire: List<Questionaire>) {
       /*
        var a =questionaire.sortedBy {
            it.title
        }
        */
        adapter.data.addAll(questionaire)
        adapter.notifyDataSetChanged()
    }

    override fun none_results(show: Boolean) {
        if (show) tv_none_questionnaires.visibility = View.VISIBLE else tv_none_questionnaires.visibility = View.GONE
    }

    override fun navigationToDetailQuestionnarie(any: Any) {
        startActivity(Intent(this, QuestionaireActivity::class.java).putExtra(QuestionaireActivity.QUESTIONNAIRE, any as Questionaire))
    }

    fun onCreateQuestionnaire(description: String, title: String) {
        presenter.onCreateQuestionaire(title, description)
    }

    override fun hideDialogNewQuestionnaire() {
        if (::newQuestionarieFragment.isInitialized) {
            newQuestionarieFragment.dismiss()
        }
    }

    override fun showButtonCreateQuestionnaire() {
        newQuestionarieFragment.showButtonCreate()
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(cl_my_repository, message, Snackbar.LENGTH_INDEFINITE).setAction("Reitentar", View.OnClickListener {
            presenter.onGetmyrepository()
        }).show()
    }

    override fun clearResults() {
        questionnaries.clear()
        adapter.notifyDataSetChanged()
    }
}
