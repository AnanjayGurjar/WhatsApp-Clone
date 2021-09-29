package android.example.whatsappclone.main_ui

import android.example.whatsappclone.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*

const val NAME = "name"
const val ID = "uid"
const val IMAGE_URL = "photo"
class ChatActivity : AppCompatActivity() {

    private val friendId by lazy{
        intent.getStringExtra(ID)
    }
    private val name by lazy {
        intent.getStringExtra(NAME)
    }
    private val image by lazy {
        intent.getStringExtra(IMAGE_URL)
    }
    private val mCurrentUID by lazy {
        FirebaseAuth.getInstance().uid
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EmojiManager.install(GoogleEmojiProvider())

        setContentView(R.layout.activity_chat)
        tv_userName.text = name
        Picasso.get().load(image).into(iv_userImageView)

    }
}