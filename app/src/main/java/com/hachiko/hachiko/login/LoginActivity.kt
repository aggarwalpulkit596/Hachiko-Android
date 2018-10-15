package com.hachiko.hachiko.login

import android.os.Bundle
import com.hachiko.hachiko.BaseActivity
import com.hachiko.hachiko.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            startActivity<OTPActivity>("PHONENO" to phoneNo.text)
        }

    }

}
