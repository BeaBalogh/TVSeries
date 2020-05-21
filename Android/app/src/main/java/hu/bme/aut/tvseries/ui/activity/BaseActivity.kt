package hu.bme.aut.tvseries.ui.activity

import android.app.ProgressDialog
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.utils.PrefUtils

abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    private val firebaseUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    val userName: String?
        get() = firebaseUser?.displayName

    val userEmail: String?
        get() = firebaseUser?.email

    val userImage: Uri?
        get() = firebaseUser?.photoUrl

    fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        PrefUtils.setUserLggedIn(false, applicationContext)
        PrefUtils.setUserID("", applicationContext)
        PrefUtils.setToken("", applicationContext)
        PrefUtils.setRole("", applicationContext)
        PrefUtils.setUserFollowed(setOf(), applicationContext)
        PrefUtils.setUserWatched(setOf(), applicationContext)
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut()

    }

    fun showProgressDialog() {
        if (progressDialog != null) {
            return
        }

        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading...")
            show()
        }
    }

    fun hideProgressDialog() {
        progressDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        progressDialog = null
    }

    protected fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
