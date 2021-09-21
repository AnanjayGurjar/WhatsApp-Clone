package android.example.whatsappclone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"
const val OTP = "otp"
class OtpActivity : AppCompatActivity() {
    lateinit var phoneNumber: String
    lateinit var otp: String
    var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    val auth = FirebaseAuth.getInstance()
    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    lateinit var mCountDownTimer: CountDownTimer
    val defaultColor = R.color.pink

    var  mCredential: PhoneAuthCredential? = null

 //   lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        phoneNumber = intent.getStringExtra(PHONE_NUMBER).toString()
        otp = intent.getStringExtra(OTP).toString()
        val userEnterdOtp = et_otp.text.toString().trim()
        tv_verifyNumber.setText("Verify $phoneNumber")
        //above line can also be implemented by first extracting the resource value of above string, give it name(say verify_string) and then use:
        // tv_verifyNumber.text = getString(R.string.verify_number, phoneNumber)

        showTimer(60000)

        spannableString()



         btn_verifyOtp.setOnClickListener {

             signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(otp, userEnterdOtp))


         }

     btn_resendOtp.setOnClickListener {
         showTimer(60000)
         val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
             override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                 val smsCode = credential.smsCode
                 et_otp.setText(smsCode)
                 startActivity(Intent(applicationContext, SignUpActivity::class.java))

             }

             override fun onVerificationFailed(e: FirebaseException) {
                 Toast.makeText(applicationContext, "Verification failed: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
             }

             override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                 mVerificationId = verificationId
             }

         }
         val options = phoneNumber?.let {
             PhoneAuthOptions.newBuilder(auth)
                 .setPhoneNumber(it)       // Phone number to verify
                 .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                 .setActivity(this)                 // Activity (for callback binding)
                 .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                 .build()
         }
         if (options != null) {
             PhoneAuthProvider.verifyPhoneNumber(options)
         }

     }

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



    //disabling the back button once the otp is asked
    override fun onBackPressed() {
        //super.onBackPressed()         now this function will not be able to call the onBackPressed and hence disabling it
    }

    private fun spannableString() {
        val span = SpannableString(getString(R.string.otpSent_changeNumber,phoneNumber))
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {
                val intent = Intent(this@OtpActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

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
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(applicationContext, "Phone number verified", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, SignUpActivity::class.java))

                }else{
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(applicationContext, "Otp entered is incorrect, please try again", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext, "Phone verification failed, please try again!!", Toast.LENGTH_SHORT).show()
                    }

                }
            }

    }

}

fun Context.createProgressDialog(message: Message, isCancelabe: Boolean): ProgressDialog{

    return ProgressDialog(this).apply {
        setCancelable(false)
        setCancelMessage(message)
        setCanceledOnTouchOutside(false)
    }
}