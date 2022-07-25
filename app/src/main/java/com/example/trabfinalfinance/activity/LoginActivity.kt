package com.example.trabfinalfinance.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabfinalfinance.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginUserName: EditText
    private lateinit var mLoginPassword: EditText
    private lateinit var mLoginSignIn: Button
    private lateinit var mRegister: TextView



    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        mLoginUserName = findViewById(R.id.login_username)
        mLoginPassword = findViewById(R.id.login_senha)

        mLoginSignIn = findViewById(R.id.login_button)
        mLoginSignIn.setOnClickListener(this)

        mRegister = findViewById(R.id.login_register_text)
        mRegister.setOnClickListener(this)



    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login_register_text -> {
                val it = Intent(this, RegistroAct::class.java)
                startActivity(it)
            }

            R.id.login_button -> {

                val email = mLoginUserName.text.toString()
                val password = mLoginPassword.text.toString()

                var isLoginFormFilled = true

                if (email.isEmpty()) {
                    mLoginUserName.error = "Este campo não pode ser nulo"
                    isLoginFormFilled = false
                }

                if (password.isEmpty()) {
                    mLoginPassword.error = "Este campo não pode ser nulo"
                    isLoginFormFilled = false
                }

                if (isLoginFormFilled) {

                    val progress = ProgressDialog(this)
                    progress.setTitle("To Do List - Aguarde")
                    progress.isIndeterminate = true
                    progress.setCancelable(false)

                    progress.show()

                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener{
                            progress.dismiss()
                            if (it.isSuccessful) {
                                val it = Intent(applicationContext, MainActivity::class.java)
                                startActivity(it)
                                finish()
                            } else {
                                showLoginErrorMessage()

                            }
                        }
                }
            }
        }

    }

    private fun showLoginErrorMessage() {
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            Toast.makeText(applicationContext, getString(R.string.login_error_message), Toast.LENGTH_SHORT).show()
        }

    }


}