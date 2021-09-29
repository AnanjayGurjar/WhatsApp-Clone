package android.example.whatsappclone.auth_registration

import android.content.Intent
import android.example.whatsappclone.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

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
            setMessage("We will be Verifying $phoneNumber , is it ok?")
            setPositiveButton("OK"){_,_ ->
                showOtpActivity()
            }
            setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra(PHONE_NUMBER, phoneNumber)
        startActivity(intent)
        finish()
    }


}