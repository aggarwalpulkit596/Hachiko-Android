package com.hachiko.hachiko.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.hachiko.hachiko.BaseActivity
import com.hachiko.hachiko.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener(View.OnClickListener {
            val intent=Intent(this,OTPActivity::class.java)
            intent.putExtra("PHONENO","+91"+phoneNo.text.toString())
            startActivity(intent)
        })

    }

}
