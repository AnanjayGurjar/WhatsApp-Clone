package android.example.whatsappclone.auth_registration

import android.content.Intent
import android.example.whatsappclone.R
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"

class OtpActivity : AppCompatActivity(), View.OnClickListener {
    var phoneNumber: String? = null
    val defaultColor = R.color.pink
    lateinit var mCountDownTimer: CountDownTimer
    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    val auth = PhoneAuthProvider.getInstance()



    var mCredential: PhoneAuthCredential? = null

    //   lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initializeViews()
        startVerification()


    }

    private fun startVerification() {
        callbacks?.let {
            auth.verifyPhoneNumber(
                phoneNumber!!,
                60,
                TimeUnit.SECONDS,
                this,
                    it
        )
        }
        showTimer(60000)
    }

    private fun initializeViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        tv_verifyNumber.text = "Verify: $phoneNumber"
        spannableString()

        btn_verifyOtp.setOnClickListener(this)
        btn_resendOtp.setOnClickListener(this)


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val smsCode = credential.smsCode
                if(!smsCode.isNullOrEmpty()){
                    et_otp.setText(smsCode)
                }
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@OtpActivity, "Verification failed due to ${p0.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
           //     super.onCodeSent(p0, p1)
                mVerificationId = verificationId
                mResendToken = token
            }

        }

    }
    private fun signInWithCredential(credential: PhoneAuthCredential){
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        startActivity(Intent(this, SignUpActivity::class.java))
                        finish()
                    }else{
                        createAlertDialog("Verification Failed! Please try again")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Verification failed: $it", Toast.LENGTH_SHORT).show()
                }
    }

    private fun spannableString() {
        val span = SpannableString(getString(R.string.otpSent_changeNumber,phoneNumber))
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                redirectToLogin()
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan, span.length-13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_changeNumber.movementMethod = LinkMovementMethod.getInstance()
        tv_changeNumber.text = span

    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showTimer(milliSec: Long) {

        tv_otpTimer.visibility = View.VISIBLE
        mCountDownTimer = object : CountDownTimer(milliSec, 1000){
            override fun onTick(millisUntilFinished: Long) {
                btn_resendOtp.isEnabled = false
                tv_otpTimer.text = "Otp sent, kindly wait for: ${millisUntilFinished/1000} sec"
            }

            override fun onFinish() {

                btn_resendOtp.isEnabled = true
                tv_otpTimer.visibility = View.GONE

                btn_resendOtp.setBackgroundColor(defaultColor)
            }

        }.start()

    }
    override fun onDestroy() {
        super.onDestroy()
        if(mCountDownTimer != null){
            mCountDownTimer.cancel()
        }
    }

    private fun createAlertDialog(message: String){
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("OK"){_,_ ->
                redirectToLogin()
            }
            setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    override fun onClick(v: View?) {
        when(v){
            btn_verifyOtp ->{
                val code = et_otp.text.toString()
                if(code.isNotEmpty() && !(mVerificationId.isNullOrEmpty())){
                    val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
                    signInWithCredential(credential)
                }

            }
            btn_resendOtp ->{
                if(mResendToken != null){
                    showTimer(60000)
                    callbacks?.let {
                        auth.verifyPhoneNumber(
                                phoneNumber!!,
                                60,
                                TimeUnit.SECONDS,
                                this,
                                it,
                                mResendToken
                        )
                    }

                }
            }
        }
    }
}
