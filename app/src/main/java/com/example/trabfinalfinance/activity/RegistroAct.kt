package com.example.trabfinalfinance.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabfinalfinance.R
import com.example.trabfinalfinance.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroAct: AppCompatActivity(), View.OnClickListener{


    private lateinit var mRegisterName: EditText
    private lateinit var mRegisterPhone: EditText
    private lateinit var mRegisterEmail: EditText
    private lateinit var mRegisterPassword: EditText
    private lateinit var mRegisterPasswordConfirmation: EditText
    private lateinit var mRegisterSignUp: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mRegisterName = findViewById(R.id.registro_nome)
        mRegisterPhone = findViewById(R.id.registro_telefone)
        mRegisterEmail = findViewById(R.id.registro_email)
        mRegisterPassword = findViewById(R.id.registro_senha)
        mRegisterPasswordConfirmation = findViewById(R.id.registro_confirma_senha)

        mRegisterSignUp = findViewById(R.id.register_button_signup)
        mRegisterSignUp.setOnClickListener(this)


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.register_button_signup -> {
                val name = mRegisterName.text.toString()
                val phone = mRegisterPhone.text.toString()
                val email = mRegisterEmail.text.toString()
                val password = mRegisterPassword.text.toString()
                val passwordConfirmation = mRegisterPasswordConfirmation.text.toString()

                var isFormRightFilled = true

                if (name.isEmpty()) {
                    mRegisterName.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if (phone.isEmpty()) {
                    mRegisterPhone.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if (email.isEmpty()) {
                    mRegisterEmail.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if (password.isEmpty()) {
                    mRegisterPassword.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if (passwordConfirmation.isEmpty()) {
                    mRegisterPasswordConfirmation.error = getString(R.string.form_is_required_error)
                    isFormRightFilled = false
                }

                if (isFormRightFilled) {
                    if (password != passwordConfirmation) {
                        mRegisterPasswordConfirmation.error = "As senhas são diferentes"
                        return
                    }
                    val handler = Handler(Looper.getMainLooper())

                    val progress = ProgressDialog(this)
                    progress.setTitle("To Do List - Aguarde")
                    progress.isIndeterminate = true
                    progress.setCancelable(false)

                    progress.show()

                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener{
                            progress.dismiss()
                            if (it.isSuccessful) {

                                val user = User(name, phone, email)

                                val ref = mDatabase.getReference("users/${mAuth.uid}")
                                ref.setValue(user)

                                handler.post{
                                    Toast.makeText(applicationContext, "O usuário ${name} foi cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                                    finish()
                                }
                            } else {
                                handler.post{
                                    Toast.makeText(applicationContext, it.exception?.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                }
            }
        }
    }







}