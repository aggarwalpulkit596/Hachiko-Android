package com.hachiko.hachiko

import android.os.Bundle
import com.hachiko.hachiko.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//should be done like this
        otpLoginBtn.setOnClickListener {
            startActivity<LoginActivity>()
        }

    }
}
