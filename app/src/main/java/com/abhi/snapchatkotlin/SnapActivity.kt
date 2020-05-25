package com.abhi.snapchatkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_snap.*
import java.io.ByteArrayOutputStream
import java.util.*

class SnapActivity : AppCompatActivity() {

    private val PICK_IMAGE = 100
    internal var imageUri: Uri? = null
    var imageView:ImageView?=null
    var choose:Button?=null
    var message:EditText?=null
    var next:Button?=null
    val imageName = UUID.randomUUID().toString() +".jpg"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        imageView = findViewById(R.id.imageView);
        choose = findViewById(R.id.choosebutton)
        message = findViewById(R.id.msgeditText)
        next = findViewById(R.id.nextbutton)
    }
    fun chooseImage(view:View){
        openGallery()
    }
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                imageUri = data.data
            }
            imageView?.setImageURI(imageUri)
        }
    }

    fun upload(view: View){

        // Get the data from an ImageView as bytes
        imageView?.isDrawingCacheEnabled = true
        imageView?.buildDrawingCache()
        val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val ref = FirebaseStorage.getInstance().getReference().child("images").child(imageName)

        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"Upload Failed",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.i("download",downloadUri.toString())

                    //moving to choose user Activity
                    var intent = Intent(this,chooseUserActivity::class.java)
                    intent.putExtra("imageURL",downloadUri.toString())
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",message?.text.toString())
                    finish()
                    startActivity(intent)

                } else {
                    // Handle failures
                    // ...
                }
            }

        }
    }
}

