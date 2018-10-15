package com.hachiko.hachiko

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.hachiko.hachiko.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        otpLoginBtn.setOnClickListener(View.OnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        })

    }
}
