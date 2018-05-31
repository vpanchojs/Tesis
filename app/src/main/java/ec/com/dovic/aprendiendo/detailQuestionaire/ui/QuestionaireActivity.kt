package ec.com.dovic.aprendiendo.detailQuestionaire.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.detailQuestionaire.QuestionnariesPresenter
import ec.com.dovic.aprendiendo.detailQuestionaire.adapter.QuestionAdapter
import ec.com.dovic.aprendiendo.detailQuestionaire.adapter.onQuestionAdapterListener
import ec.com.dovic.aprendiendo.entities.Question
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.newQuestion.ui.QuestionActivity
import ec.com.dovic.aprendiendo.questionnaireResume.servicie.DonwloadIntentService
import ec.com.dovic.aprendiendo.updateQuestionnaire.ui.UpdateQuestionnaireActivity
import ec.com.dovic.aprendiendo.util.BaseActivitys
import kotlinx.android.synthetic.main.activity_questionaire.*
import javax.inject.Inject


class QuestionaireActivity : AppCompatActivity(), View.OnClickListener, onQuestionAdapterListener, QuestionnaireView {

    var questionList: ArrayList<Question>? = ArrayList<Question>()
    lateinit var application: MyApplication
    lateinit var adapter: QuestionAdapter
    lateinit var questionaire: Questionaire

    @Inject
    lateinit var presenter: QuestionnariesPresenter

    companion object {
        val QUESTIONNAIRE = "questionnaire"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionaire)
        setupInjection()

        setupRecyclerView()
        if (intent.extras != null) {
            questionaire = intent.extras.getParcelable(QUESTIONNAIRE)
        }

        setupToolBar()
        setDataQuestionire();
        fab_new_question.setOnClickListener(this)
    }

    private fun setDataQuestionire() {
        tie_title.setText(questionaire.title)
        tie_discription.setText(questionaire.description)
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getQuestionnarieComponent(this).inject(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.onGetDataQuestionnaire(questionaire.idCloud)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setupRecyclerView() {
        adapter = QuestionAdapter(questionList!!, this)
        val mDividerItemDecoration = DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        rv_questions.addItemDecoration(mDividerItemDecoration)
        rv_questions.layoutManager = LinearLayoutManager(this)
        rv_questions.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail_questionaire, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                createDeleteDialog().show()
            }
            R.id.action_update -> {
                startActivity(Intent(this, UpdateQuestionnaireActivity::class.java).putExtra(UpdateQuestionnaireActivity.PARAM_QUESTIONNAIRE, questionaire))
            }

            R.id.action_download -> {
                presenter.isExistQuestionnnaireLocal(questionaire.idCloud)
                //CONTROLAR SI DESEA REMPLAZAR EL CUESTIONARIO
            }
            android.R.id.home -> {
                navigationBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun createDeleteDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Mensaje de confirmación")
                .setMessage("¿Desea eliminar el cuestionario?")
                .setPositiveButton("ACEPTAR"
                ) { _, which ->
                    presenter.onDeleteQuestionnnaire(questionaire.idCloud)
                }
                .setNegativeButton("CANCELAR",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {}
                        })
        return builder.create()
    }


    fun createDeleteDialogDuplicateQuestionnaire(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Mensaje de confirmación")
                .setMessage("El cuestionario ya existe en su repositorio local. ¿Desea duplicarlo?")
                .setPositiveButton("ACEPTAR"
                ) { _, which ->
                    // presenter.onDeleteQuestionnnaire(questionaire.idCloud)
                    dowloadQuestionnaire()
                }
                .setNegativeButton("CANCELAR",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {}
                        })
        return builder.create()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_new_question -> {
                val intent = Intent(this, QuestionActivity::class.java)
                intent.putExtra(QuestionActivity.ID_QUESTIONNAIRE_PARAM, questionaire.idCloud)
                startActivity(intent)
            }
        /*
        R.id.fab_edit_questionnaire -> {
            questionaire.description = tie_discription.text.toString()
            questionaire.title = tie_title.text.toString()
            presenter.updateBasicQuestionnaire(questionaire)
        }
        */
        }
    }

    override fun navigationToDetailQuestion(any: Any, position: Int) {
        val intent = Intent(this, QuestionActivity::class.java)
        intent.putExtra(QuestionActivity.ID_QUESTIONNAIRE_PARAM, questionaire.idCloud)
        intent.putExtra(QuestionActivity.QUESTION_PARAM, (any as Question))
        intent.putExtra(QuestionActivity.POSITION_PARAM, position)
        //startActivityForResult(intent, QuestionActivity.REQUEST_CODE)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (resultCode) {
            1 -> {
                /*
                val question = data.extras!!.getParcelable(QuestionActivity.ID_QUESTIONNAIRE_PARAM) as Question
                val pos = data.extras!!.getInt(QuestionActivity.POSITION_PARAM)
                if (pos >= 0) {
                    questionList!!.set(pos, question)
                    adapter.notifyItemChanged(pos)
                } else {
                    questionList!!.add(question)
                    adapter.notifyDataSetChanged()

                }
                //question.questionaire
                question.questionaire.target = questionaire
                presenter.onSaveQuestion(questionaire.idQuestionaire, question)
                */
                Toast.makeText(this, "Pregunta agregada correctamente", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun hideProgressDialog() {
        BaseActivitys.hideProgressDialog()
    }

    override fun showProgressDialog(message: Any) {
        BaseActivitys.showProgressDialog(this, message)
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(this, message, Toast.LENGTH_SHORT)
    }


    override fun setQuestions(questionList: List<Question>) {
        this.questionList!!.clear()
        this.questionList!!.addAll(questionList)
        adapter.notifyDataSetChanged()
    }

    override fun showProgress(visibility: Int) {
        progressbar.visibility = visibility
    }

    override fun none_results(show: Boolean) {
        if (show)
            tv_none_questions.visibility = View.VISIBLE
        else
            tv_none_questions.visibility = View.GONE

    }

    override fun navigationBack() {
        finish()
    }

    override fun dowloadQuestionnaire() {
        if (questionaire.numberQuest > 0) {
            val intent = Intent(this, DonwloadIntentService::class.java)
            intent.putExtra(DonwloadIntentService.IDQUESTIONNAIRE, questionaire.idCloud)
            startService(intent)
            showMessagge("Descargando Cuestionario")
            finish()
        } else {
            showMessagge("No tiene suficientes preguntas")
        }
    }

    override fun confirmDownloadQuestionnaire() {
        createDeleteDialogDuplicateQuestionnaire().show()
    }
}
