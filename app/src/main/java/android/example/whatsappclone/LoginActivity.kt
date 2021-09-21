package android.example.whatsappclone

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    var phoneNumber: String? = null
    var countryCode: String? = null
    val auth = FirebaseAuth.getInstance()
    val TAG = "TAG"
    var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //still need to implement hint request for the phone number

        et_phoneNumber.addTextChangedListener {
            btn_verify.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }
        btn_verify.setOnClickListener {
            checkNumber()

        }
    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus.toString()

        phoneNumber = countryCode + et_phoneNumber.text.toString()
        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will send the otp on Phone Number: $phoneNumber, do you want to edit?")
            setPositiveButton("OK") { _, _ ->
                verifyUser()
                sendVerificationCode()
            }
            setNegativeButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun verifyUser() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(TAG, "onVerificationFailed  $e")
                Toast.makeText(
                    applicationContext,
                    "Verification failed: ${e.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                val intent = Intent(applicationContext, OtpActivity::class.java)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                intent.putExtra(OTP, verificationId)
                startActivity(intent)
                finish()

            }

        }


    }

    private fun sendVerificationCode() {
        val options = phoneNumber?.let {
            callbacks?.let { it1 ->
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(it)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(it1)          // OnVerificationStateChangedCallbacks
                    .build()
            }
        }
        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}