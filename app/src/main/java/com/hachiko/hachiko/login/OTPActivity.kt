package com.hachiko.hachiko.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Telephony
import android.support.v4.content.ContextCompat
import android.telephony.SmsMessage
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.hachiko.hachiko.BaseActivity
import com.hachiko.hachiko.R
import com.hachiko.hachiko.sharedPreferences.PrefManager
import com.hachiko.hachiko.utils.Otpreciver
import kotlinx.android.synthetic.main.activity_otp.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class OTPActivity : BaseActivity() {

    lateinit var phoneNo:String
    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var isAllowedToRead:Boolean=false
    lateinit var intentFilter: IntentFilter
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId:String=""
    private var mVerificationProgress=false
    lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var reciever: Otpreciver
    lateinit var messageotp:String
    lateinit var otpmessage:String
    var mtimer: CountDownTimer? = null
    lateinit var prefManager:PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val i =intent
        phoneNo=i.getStringExtra("PHONENO")

        otpSentToNumber.text=getString(R.string.otp_sent_to_number)+" ${phoneNo}"

        mAuth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        if(!checkPermission()){
            requestPermission()
        }

        prefManager= PrefManager(this)

        intentFilter= IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        intentFilter.priority=100

        mCallbacks=object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                mVerificationId= p0!!
                mResendToken= p1!!
                mVerificationProgress=true
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException?) {
                Log.d("Verification FAILED",p0.toString())
                if (p0 is FirebaseTooManyRequestsException) {
                    toast("Quota exceeded")
                    mVerificationProgress = false

                }
            }
        }

        loginBtn.isEnabled=false
        loginBtn.setOnClickListener{
            settimer()
            toast(getString(R.string.otp_resent))
            resendVerificationCode(phoneNo,mResendToken)
            loginBtn.isEnabled=false
        }

        phoneAuth(phoneNo)
        settimer()
        verifyOtp()

        backButton.setOnClickListener{
            finish()
        }

    }

    private fun verifyOtp() {
        reciever=object : Otpreciver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                super.onReceive(p0, p1)
                val data = p1?.extras
                lateinit var pdus: Array<Any>
                if (data != null) {
                    try {
                        pdus = data.get("pdus") as Array<Any>
                    } catch (e: Exception) {
                    }
                }
                for (pdu in pdus) { // loop through and pick up the SMS of interest
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    val abcd = smsMessage.messageBody.replace("[^0-9]", "")
                    messageotp = abcd
                    otpmessage=messageotp.split(" ")[0]
                }
                Toast.makeText(this@OTPActivity,messageotp,Toast.LENGTH_SHORT).show()
                Log.d("OTP","MESSAGE "+messageotp.split(" ")[0])
                otpedittext.setText(otpmessage)
                mtimer!!.cancel()
                resendOTP.text=getString(R.string.otp_recieved)
                loginBtn.isEnabled=false
            }
        }
        this@OTPActivity.registerReceiver(reciever,intentFilter)
    }

    private fun resendVerificationCode(phoneNumber: String,
                                       token: PhoneAuthProvider.ForceResendingToken) {
        Toast.makeText(this, "OTP Request sent", Toast.LENGTH_SHORT).show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks, // OnVerificationStateChangedCallbacks
                token)             // ForceResendingToken from callbacks
    }

    private fun phoneAuth(phoneNo: String?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo!!,  //phone number
                60, // time to wait
                TimeUnit.SECONDS, // unit of time
                this, //context
                mCallbacks //callback on verification changed
        )
    }

    private fun signInWithPhoneAuthCredential(p0: PhoneAuthCredential?) {
        mAuth.signInWithCredential(p0!!)
                .addOnCompleteListener({
                    if (it.isSuccessful){
                        db.collection(getString(R.string.users)).document(mAuth.uid!!)
                                .get()
                                .addOnCompleteListener({
                                    if(it.result!!.exists()){
                                        resendOTP.text=getString(R.string.otp_verified)
                                        loginBtn.isEnabled=false
                                        prefManager.saveString(PrefManager.SIGN_IN_METHOD,getString(R.string.otp_sign_in))
                                        showToast(getString(R.string.user_exist))
                                    }else{
                                        val signupIntent=Intent(this,SignupActivity::class.java)
                                        startActivity(signupIntent)
                                    }
                                })
                    }else{
                        if (it.exception is FirebaseAuthInvalidCredentialsException){
                            showToast(getString(R.string.otp_incorrect))
                        }
                    }
                })
    }

    private fun checkPermission() : Boolean{
        val readSMS= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        isAllowedToRead=readSMS== PackageManager.PERMISSION_GRANTED
        return isAllowedToRead
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_SMS), 2501)
        }
    }

    private fun showToast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }

    private fun settimer() {
        mtimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                resendOTP.text="Resend (" + String.format("%02d", seconds) + " sec)"
            }

            override fun onFinish() {
                resendOTP.text=getString(R.string.resend_otp)
                loginBtn.isEnabled=true
            }
        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        mtimer?.cancel()
        this@OTPActivity.unregisterReceiver(reciever)

    }

    override fun onBackPressed() {
       // super.onBackPressed()
        finish()
    }

}
