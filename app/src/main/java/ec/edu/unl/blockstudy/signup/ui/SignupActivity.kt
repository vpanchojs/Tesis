package ec.edu.unl.blockstudy.signup.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.main.MainActivity
import ec.edu.unl.blockstudy.signup.SignupPresenter
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.validateFieldEmail
import kotlinx.android.synthetic.main.activity_signup.*
import javax.inject.Inject


class SignupActivity : AppCompatActivity(), SignupView, View.OnClickListener {

    @Inject
    lateinit var presenter: SignupPresenter

    lateinit var progressDialog: ProgressDialog
    lateinit var application: MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        btn_sigUp.setOnClickListener(this)
        setupToolBar()
        setupInjection()
        setupFieldsValidation()
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_name)
        fields.add(tie_lastname)
        fields.add(tie_comment)
        fields.add(tie_password)
        onTextChangedListener(fields, btn_sigUp)
    }


    override fun showMessagge(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showProgressDialog(message: Int) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(message));
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    override fun navigationToMain() {
        Log.e("SingUp", "Iniciando session")
        startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }


    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getSignupComponent(this).inject(this)
    }


    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }


    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }


    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.SignUp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_sigUp -> {
                if (validateFieldEmail(this, tie_comment)) {
                    presenter.onSignUp(
                            tie_name.text.toString(),
                            tie_lastname.text.toString(),
                            tie_comment.text.toString(),
                            tie_password.text.toString()
                    )
                }
            }
        }
    }
}

