package ec.com.dovic.aprendiendo.login.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.login.LoginPresenter
import ec.com.dovic.aprendiendo.main.ui.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), View.OnClickListener, LoginView, RecoveryPasswordFragment.OnRecoveryPasswordListener {
    val TAG = "LoginActivity"

    lateinit var application: MyApplication
    lateinit var progressDialog: ProgressDialog

    private var callbackManager: CallbackManager? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null

    val SIGN_IN_CODE = 888


    @Inject
    lateinit var presenter: LoginPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_login)
        // cl_chip_registre.setOnClickListener(this)
        //btn_sigin.setOnClickListener(this)
        //btn_forget_password.setOnClickListener(this)
        setupSingInFacebook()
        setupSingInGoogle()
        btn_sigin_google.setOnClickListener(this)
        btn_sigin_facebook.setOnClickListener(this)
        setupInjection()
        // setupFieldsValidation()
    }

    /*
    private fun setupFieldsValidation() {
        var fields = ArrayList<TextInputEditText>()
        fields.add(tie_comment)
        fields.add(tie_password)
        onTextChangedListener(fields, btn_sigin)
    }
    */

    override fun onStart() {
        super.onStart()
        presenter.onResume()
        presenter.onInSession()
    }

    override fun onStop() {
        super.onStop()
        presenter.onPause()
        presenter.onInSessionRemove()
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getLoginComponent(this).inject(this)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.btn_sigin_google -> {
                val intent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(intent, SIGN_IN_CODE)
                showMessagge("Iniciando sesión con google")
            }
            R.id.btn_sigin_facebook -> {
                showMessagge("Iniciando sesión con facebook")
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

            }

        /*
            R.id.btn_sigin -> {
                //if (validateFieldEmail(this, tie_comment))
                //   presenter.onSignIn(tie_comment.text.toString(), tie_password.text.toString())
            }
            R.id.cl_chip_registre -> {
                startActivity(Intent(this, SignupActivity::class.java))
            }
            R.id.btn_forget_password -> {
                val recoveryPasswordFragment = RecoveryPasswordFragment.newInstance()
                recoveryPasswordFragment.show(supportFragmentManager, "Recuperar Contrasena")
            }
        */
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
//        progressDialog.dismiss()
    }

    override fun navigationMain() {
        startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()

    }

    override fun onRecoveryPassword(email: String) {
        presenter.onRecoveryPassword(email)
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(cl_body, message, Snackbar.LENGTH_INDEFINITE).setAction("Reenviar", View.OnClickListener {
            presenter.sendEmailVerify()
        }).show()
    }


    fun setupSingInFacebook() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                if (Profile.getCurrentProfile() == null) {
                    object : ProfileTracker() {
                        override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                            presenter.tokenFacebook(loginResult.accessToken.token, currentProfile!!.firstName, currentProfile!!.lastName, "", currentProfile.getProfilePictureUri(200, 200))
                            stopTracking()
                        }
                    }
                } else {
                    presenter.tokenFacebook(loginResult.accessToken.token, Profile.getCurrentProfile().firstName, Profile.getCurrentProfile().lastName, "", Profile.getCurrentProfile().getProfilePictureUri(200, 200))
                }
            }

            override fun onCancel() {
                showMessagge("Incio de sesión cancelado")
            }

            override fun onError(error: FacebookException) {
                //Toast.makeText(context, R.string.error_login, Toast.LENGTH_SHORT).show()
                showMessagge("Error al autenticarse con facebook, intentelo nuevamente")
            }
        })

    }

    fun setupSingInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("247734562267-8q463r4a1gi0bbhi3faujjkf3gvl6r8p.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(result: Task<GoogleSignInAccount>) {
        try {
            val account = result.getResult(ApiException::class.java)
            Log.e(TAG, "signInResult:succes idtoken= ${account.givenName}")
            // showMessage("Session Correctamente")
            presenter.tokenGoogle(account.idToken!!, account.givenName, account.familyName, account.email, account.photoUrl)
        } catch (e: Exception) {
            showMessagge("Error al autenticarse en google")
            Log.e(TAG, "signInResult:failed code=" + e.toString());
        }
    }

    override fun showButtonSignIn(visible: Int, message: String) {
        btn_sigin_facebook.visibility = visible
        btn_sigin_google.visibility = visible
        tv_message_login.text = message
    }

    override fun showProgress(visible: Int) {
        progressbar.visibility = visible
    }
}
