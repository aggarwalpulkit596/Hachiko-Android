package com.hachiko.hachiko.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.davidmiguel.numberkeyboard.NumberKeyboard
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.hachiko.hachiko.BaseActivity
import com.hachiko.hachiko.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val numberKeyboard=findViewById<NumberKeyboard>(R.id.keyboard)

        numberKeyboard.setListener(object : NumberKeyboardListener {
            override fun onNumberClicked(p0: Int) {
                if (phoneNo.text.length<10){
                    showPhoneData(phoneNo.text.append(p0.toString()).toString())
                }
            }

            override fun onLeftAuxButtonClicked() {

            }

            override fun onRightAuxButtonClicked() {
                if (phoneNo.text.isNotBlank()){
                    showPhoneData(phoneNo.text.substring(0,phoneNo.text.length-1))
                }
            }
        })

        phoneNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.length==10){
                    startActivity<OTPActivity>("PHONENO" to "+91${phoneNo.text}")
                    finish()
                }
            }
        })


        backButton.setOnClickListener{
            onBackPressed()
        }

    }

    private fun showPhoneData(phoneData:String){
        phoneNo.text=Editable.Factory.getInstance().newEditable(phoneData)
    }

    override fun onBackPressed() {
       // super.onBackPressed()
        finish()
    }

}
