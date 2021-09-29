package android.example.whatsappclone

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

const val DATA_USER = "users"
class SignUpActivity : AppCompatActivity() {

    val firebaseStorage = FirebaseStorage.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val database = FirebaseFirestore.getInstance()
    lateinit var downloadUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //try to use firebase extensions to make thumb image

        iv_profilePhoto.setOnClickListener {
            checkPermissionForImage()
        }
        btn_next.setOnClickListener {
            btn_next.isEnabled = false
            val name = et_name.text.toString()
            if(name.isNotEmpty()){
                et_name.setError("Field is Mandatory")
            }else if(!::downloadUrl.isInitialized){
                Toast.makeText(this, "Please set a profile picture", Toast.LENGTH_SHORT).show()
            }else{
                val user = User(name, downloadUrl, downloadUrl, firebaseAuth.uid!!)
                database.collection(DATA_USER).document(firebaseAuth.uid!!).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            btn_next.isEnabled = true
                        }
            }
        }
    }
    private fun checkPermissionForImage() {
        if ((checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) &&
            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            val permission_read = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            val permission_write = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            requestPermissions(
                permission_read,
                100
            )
            requestPermissions(
                permission_write,
                200
            )
        }else{
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            300
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 300){
            data?.data.let {
                iv_profilePhoto.setImageURI(it)
                uploadImageToFirebase(it)
            }
        }

    }

    private fun uploadImageToFirebase(uri: Uri?) {

        btn_next.isEnabled = false
        val ref = firebaseStorage.reference.child("uploads/${firebaseAuth.uid.toString()}")
        val uploadTask = uri?.let { ref.putFile(it) }
        if (uploadTask != null) {
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->

                if (!task.isSuccessful){
                    task.exception.let {
                        throw it!!
                    }
                }else{
                    return@Continuation ref.downloadUrl
                }

            }).addOnCompleteListener {task->
                btn_next.isEnabled = true
                if (task.isSuccessful){
                    downloadUrl = task.result.toString()
                    Log.d("URL", "Download Url: $downloadUrl")
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }


    }
}