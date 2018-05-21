package ec.edu.unl.blockstudy.newQuestionnaire.ui

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Questionaire
import ec.edu.unl.blockstudy.newQuestionnaire.NewQuestionairePresenter
import ec.edu.unl.blockstudy.util.BaseActivitys
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.activity_new_questionnaire.*
import javax.inject.Inject

class NewQuestionnaireActivity : AppCompatActivity(), View.OnClickListener, NewQuestionaireView {
    companion object {
        val PARAM_QUESTIONNAIRE = "questionnaire"
        val REQUEST_INFORM_BASIC_QUESTIONNAIRE = 1
    }


    lateinit var application: MyApplication
    lateinit var questionaire: Questionaire

    @Inject
    lateinit var presenter: NewQuestionairePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_questionnaire)
        setupInjection()
        setupFieldsValidation()
        setupToolBar()
        btn_create!!.setOnClickListener(this)
        /*btn_add_keyword.setOnClickListener(this)
        rv_subjects.layoutManager = LinearLayoutManager(this)
        rv_subjects.adapter = keywordAdapter
        */
        if (intent.extras != null) {
            questionaire = intent.extras.getParcelable(PARAM_QUESTIONNAIRE)
            setDataQuestionnaire()
        }
    }

    private fun setDataQuestionnaire() {
        tie_title.setText(questionaire.title)
        tie_discription.setText(questionaire.description)

        when (questionaire.difficulty) {
            0 -> {
                rb_difificulty_basic.isChecked = true
            }
            1 -> {
                rb_difificulty_intermediate.isChecked = true
            }
            2 -> {
                rb_difificulty_advanced.isChecked = true
            }

        }
        //Log.e("q", "" + questionaire.keyword.size)
        //data!!.addAll(questionaire.keyword)
        //keywordAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getNewQuestionaireComponent(this).inject(this)
    }

    private fun setupToolBar() {
        setSupportActionBar(tb_new_quest)
        supportActionBar?.title = getString(R.string.tv_upload_questionnaire)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_title!!)
        fields.add(tie_discription!!)
        onTextChangedListener(fields, btn_create!!)
    }

    private fun verifyNoneKeyword() {
        //if (data!!.size > 0) tv_none_keyword.visibility = View.GONE else tv_none_keyword.visibility = View.VISIBLE
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {

                questionaire.difficulty = getDifficulty()
                questionaire.keywords = tie_keyword.text.toString()
                presenter.onUploadQuestionaire(questionaire)
                //presenter.onUploadQuestionaire(tie_title.text.toString(), tie_discription.text.toString(), getDifficulty(), data!!, questionaire.idQuestionaire)

            }

        /*R.id.btn_add_keyword -> {
            val fragment = KeyWordFragment.newInstance()
            fragment.show(supportFragmentManager, "Recuperar Contrasena")
        }*/
        }
    }

    fun getDifficulty(): Int {
        when (rg_difficulty.checkedRadioButtonId) {
            R.id.rb_difificulty_basic -> {
                return 0
            }
            R.id.rb_difificulty_intermediate -> {
                return 1
            }
            R.id.rb_difificulty_advanced -> {
                return 2
            }
            else -> {
                return 0
            }
        }
    }


    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(this, message, Toast.LENGTH_SHORT)
    }

    override fun showProgressDialog(message: Any) {
        BaseActivitys.showProgressDialog(this, message)
    }

    override fun hideProgressDialog() {
        BaseActivitys.hideProgressDialog()
    }

    override fun navigationToQuestionaire() {
        //startActivity(Intent(this, QuestionaireActivity::class.java).putExtra(QuestionaireActivity.QUESTIONNAIRE, questionaire as Questionaire))
        finish()
    }
}
