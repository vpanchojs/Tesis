package ec.com.dovic.aprendiendo.updateQuestionnaire.ui

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.RadioGroup
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Questionaire
import ec.com.dovic.aprendiendo.updateQuestionnaire.UpdateQuestionairePresenter
import ec.com.dovic.aprendiendo.util.BaseActivitys
import ec.com.dovic.aprendiendo.util.BaseActivitys.Companion.onTextChangedListener
import kotlinx.android.synthetic.main.activity_new_questionnaire.*
import javax.inject.Inject

class UpdateQuestionnaireActivity : AppCompatActivity(), View.OnClickListener, UpdateQuestionaireView, RadioGroup.OnCheckedChangeListener {

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {

        when (p1) {
            R.id.rb_private -> {

            }
            R.id.rb_public -> {
                if (questionaire.numberQuest <= 0) {
                    showMessagge("No tiene suficientes preguntas")
                    rb_private.isChecked = true
                }
            }

        }
    }

    companion object {
        val PARAM_QUESTIONNAIRE = "questionnaire"
        val REQUEST_INFORM_BASIC_QUESTIONNAIRE = 1
    }


    lateinit var application: MyApplication
    lateinit var questionaire: Questionaire
    var category: String = "Otra"

    @Inject
    lateinit var presenter: UpdateQuestionairePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_questionnaire)
        setupInjection()
        setupFieldsValidation()
        setupToolBar()
        setupEvent()
        if (intent.extras != null) {
            questionaire = intent.extras.getParcelable(PARAM_QUESTIONNAIRE)
            setDataQuestionnaire()
        }


    }

    private fun setupEvent() {
        btn_select_category.setOnClickListener(this)
        rg_visibility.setOnCheckedChangeListener(this)
        btn_create!!.setOnClickListener(this)
    }

    private fun setDataQuestionnaire() {
        tie_title.setText(questionaire.title)
        tie_discription.setText(questionaire.description)
        tie_keyword.setText(questionaire.keywords)
        setDifficulty(questionaire.difficulty)
        publicOrPrivate(questionaire.post)
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
        supportActionBar?.title = getString(R.string.update_questionnaire)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {
                questionaire.difficulty = getDifficulty()
                questionaire.keywords = tie_keyword.text.toString()
                questionaire.post = getPublicOrPrivate()
                questionaire.subject = category
                Log.e("update", getPublicOrPrivate().toString())
                presenter.onUploadQuestionaire(questionaire)
                //presenter.onUploadQuestionaire(tie_title.text.toString(), tie_discription.text.toString(), getDifficulty(), data!!, questionaire.idQuestionaire)
            }
            R.id.btn_select_category -> {
                val popmenu = PopupMenu(this, btn_select_category)
                popmenu.menuInflater.inflate(R.menu.menu_select_category, popmenu.menu)

                popmenu.setOnMenuItemClickListener {
                    btn_select_category.text = it.title
                    category = it.title.toString()
                    return@setOnMenuItemClickListener true
                }

                popmenu.show()

            }
        }
    }


    fun publicOrPrivate(post: Boolean) {
        when (post) {
            true -> {
                rb_public.isChecked = true
            }
            false -> {
                rb_private.isChecked = true
            }
        }
    }

    fun getPublicOrPrivate(): Boolean {
        when (rg_visibility.checkedRadioButtonId) {
            R.id.rb_public -> {
                return true
            }
            R.id.rb_private -> {
                return false
            }
            else -> {
                return false
            }
        }
    }


    fun setDifficulty(dificulty: Int) {
        when (dificulty) {
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
        finish()
    }
}
