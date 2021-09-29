package android.example.whatsappclone.main_ui

import android.content.Intent
import android.example.whatsappclone.auth_registration.LoginActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LoginActivity::class.java))
        if(firebaseAuth.currentUser == null){

        }else{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}