package com.abhi.snapchatkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class chooseUserActivity : AppCompatActivity() {

    var userListView:ListView ?=null
    var emails:ArrayList<String> = ArrayList()
    var keys:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)
        userListView = findViewById(R.id.userListView)
        var adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emails)
        userListView?.adapter=adapter

        //firebase database
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object :ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var email = p0.child("email").value as String
                emails.add(email)
                p0.key?.let { keys.add(it) }
                adapter.notifyDataSetChanged()

            }
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })

        userListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            var intent:Intent = getIntent()
            val snapMap: Map<String, String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,"imageName" to intent.getStringExtra("imageName"),"imageURL" to intent.getStringExtra("imageURL"),"mesaage" to intent.getStringExtra("message"))
            FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap)

            Log.i("imageUrl",intent.getStringExtra("ImageURL"))
            val intent1 = Intent(this,SnapsActivity::class.java)
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(intent1)
        }
    }
}
