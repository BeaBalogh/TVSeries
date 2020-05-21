package hu.bme.aut.tvseries.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.AuthResponse
import hu.bme.aut.tvseries.utils.PrefUtils


class LoginActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val googleButton: SignInButton = findViewById(R.id.btnGoogleLogin)
        googleButton.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            loginOnBackend()
        }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        Log.w(TAG, "Google sign in")

        startActivityForResult(
            signInIntent,
            RC_SIGN_IN
        )
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Gooogle Auth failed", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    loginOnBackend()

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT)
//                        .show()
                }
                hideProgressDialog()
            }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGoogleLogin -> signIn()
            R.id.btnSkip -> navigateToMain()
        }

    }

    private fun loginOnBackend() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result!!.token!!
                    PrefUtils.setToken(token, applicationContext)
                    BackendInteractor(applicationContext).login({
                        PrefUtils.setUserLggedIn(true, applicationContext)
                        PrefUtils.setUserID(mUser.email!!, applicationContext)
                        saveToPref(it)
                        navigateToMain()
                    }, {
                        firebaseAuth.signOut()
                        Log.i("mylog ROLE", "ERROR")
                    })

                }
            }
    }

    private fun saveToPref(response: AuthResponse) {
        PrefUtils.setRole(response.role, applicationContext)
        PrefUtils.setUserFollowed(
            response.followed.map { it.toString() }.toSet(),
            applicationContext
        )

        PrefUtils.setUserWatched(
            response.watched.map { it.toString() }.toSet(),
            applicationContext
        )
    }

    private fun navigateToMain() {
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

}
