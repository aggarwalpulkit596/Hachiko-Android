package com.hachiko.hachiko.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hachiko.hachiko.BaseActivity
import com.hachiko.hachiko.R
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity() {

    lateinit var dogName:String
    lateinit var dogBreed:String
    lateinit var ownerName:String
    lateinit var ownerLoc:String
    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private val userData= mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        signUpbtn.setOnClickListener(View.OnClickListener {
            dogName=dogNameEditText.text.toString()
            dogBreed=dogBreedEditText.text.toString()
            ownerName=ownerNameEditText.text.toString()
            ownerLoc=ownerlocationEditText.text.toString()

            if (dogName.isNotBlank() && dogBreed.isNotBlank() && ownerName.isNotBlank() && ownerLoc.isNotBlank()){
                userData.put(getString(R.string.dog_name),dogName)
                userData.put(getString(R.string.dog_breed),dogBreed)
                userData.put(getString(R.string.owner_name),ownerName)
                userData.put(getString(R.string.owner_loc),ownerLoc)
                db.collection(getString(R.string.users)).document(mAuth.uid!!)
                        .set(userData as Map<String, Any>)
                        .addOnCompleteListener(OnCompleteListener {
                            if (it.isSuccessful){
                                showToast(getString(R.string.user_added_successfully))
                            }else{
                                showToast(getString(R.string.user_addition_failed))
                            }
                        })
            }else{
                showToast(getString(R.string.all_fields_are_complusory))
            }

        })

    }

    fun showToast(message:String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

}
