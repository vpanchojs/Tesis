package ec.edu.unl.blockstudy.login.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.login.LoginPresenter
import ec.edu.unl.blockstudy.main.MainActivity
import ec.edu.unl.blockstudy.signup.ui.SignupActivity
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.onTextChangedListener
import ec.edu.unl.blockstudy.util.BaseActivitys.Companion.validateFieldEmail
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), View.OnClickListener, LoginView, RecoveryPasswordFragment.OnRecoveryPasswordListener {
    val TAG = "LoginActivity"

    lateinit var application: MyApplication
    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var presenter: LoginPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_login)
        cl_chip_registre.setOnClickListener(this)
        btn_sigin.setOnClickListener(this)
        btn_forget_password.setOnClickListener(this)
        setupInjection()
        setupFieldsValidation()
    }

    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_comment)
        fields.add(tie_password)
        onTextChangedListener(fields, btn_sigin)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.onInSession()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
        presenter.onInSessionRemove()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getLoginComponent(this).inject(this)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_sigin -> {
                if (validateFieldEmail(this, tie_comment))
                    presenter.onSignIn(tie_comment.text.toString(), tie_password.text.toString())
            }
            R.id.cl_chip_registre -> {
                startActivity(Intent(this, SignupActivity::class.java))
            }
            R.id.btn_forget_password -> {
                val recoveryPasswordFragment = RecoveryPasswordFragment.newInstance()
                recoveryPasswordFragment.show(supportFragmentManager, "Recuperar Contrasena")
            }
        }
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

    override fun hideProgressDialog() {
        progressDialog.hide()
    }

    override fun navigationMain() {
        startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    override fun onRecoveryPassword(email: String) {
        presenter.onRecoveryPassword(email)
    }
}
