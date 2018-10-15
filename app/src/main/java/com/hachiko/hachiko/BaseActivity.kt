package com.hachiko.hachiko

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //no need to set full screen in base activity can be done using app theme 
        
//         window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                 WindowManager.LayoutParams.FLAG_FULLSCREEN)

        supportActionBar!!.hide()
    }
}
