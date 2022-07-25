package com.example.trabfinalfinance.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.trabfinalfinance.R
import com.example.trabfinalfinance.model.Conta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class RecDespAct : AppCompatActivity(), View.OnClickListener{
    private lateinit var mContaFormTitle: TextView
    private lateinit var mContaFormName: EditText
    private lateinit var mContaFormValor: EditText
    private lateinit var mContaGroupRadioButton: RadioGroup
    private lateinit var mContaFormReceita: RadioButton
    private lateinit var mContaFormDespesa: RadioButton
    private lateinit var mContaFormSave: Button
    var mCreateAt: String = ""

    private lateinit var mContaId: String
    private lateinit var mTipo: String

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rec_desp)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mContaId = intent.getStringExtra("contaId") ?: ""
        mTipo = intent.getStringExtra("tipo") ?: ""

        mContaFormTitle = findViewById(R.id.titleRec_Des)
        mContaFormName = findViewById(R.id.recdes_nome)
        mContaFormValor = findViewById(R.id.recdes_valor)
        mContaGroupRadioButton = findViewById(R.id.radioGroup)
        mContaFormReceita = findViewById(R.id.receita_rad_button)
        mContaFormDespesa = findViewById(R.id.despesa_rad_button)

        mContaFormSave = findViewById(R.id.recdes_save_butt)
        mContaFormSave.setOnClickListener(this)

        if (mContaId.isNotEmpty()) {

            mContaFormTitle.text = "Editar Conta"

            if (mTipo == "despesa") {
                val query =
                    mDatabase.reference.child("users/${mAuth.uid}/despesas/${mContaId}").orderByKey()
                query.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val conta = snapshot.getValue(Conta::class.java)!!

                        handler.post {
                            mContaFormTitle.text =
                                Editable.Factory.getInstance().newEditable("Editar Conta")
                            mContaFormName.text =
                                Editable.Factory.getInstance().newEditable(conta.name)
                            mContaFormValor.text =
                                Editable.Factory.getInstance().newEditable(conta.valor)
                            if (mTipo == "despesa") {
                                mContaFormDespesa.isChecked = true
                            }
                            mCreateAt = conta.createAt
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                })
            } else {
                val query2 =
                    mDatabase.reference.child("users/${mAuth.uid}/receitas/${mContaId}").orderByKey()
                query2.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val conta = snapshot.getValue(Conta::class.java)!!

                        handler.post {
                            mContaFormTitle.text =
                                Editable.Factory.getInstance().newEditable("Editar Conta")
                            mContaFormName.text =
                                Editable.Factory.getInstance().newEditable(conta.name)
                            mContaFormValor.text =
                                Editable.Factory.getInstance().newEditable(conta.valor)
                            if (mTipo == "receita") {
                                mContaFormReceita.isChecked = true
                            }

                            mCreateAt = conta.createAt
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
            mContaFormDespesa.isClickable = false
            mContaFormReceita.isClickable = false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.recdes_save_butt -> {

                val name = mContaFormName.text.toString()
                val valor = mContaFormValor.text.toString()
                val receita = mContaFormReceita.isChecked
                val despesa = mContaFormDespesa.isChecked
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val createAt = sdf.format(Date()).toString()



                if (name.isEmpty()) {
                    mContaFormName.error = "Este campo não pode ser vazio"
                    return
                }

                if (valor.isEmpty()) {
                    mContaFormValor.error = "Este campo não pode ser vazio"
                    return
                }

                if (receita == false && despesa == false) {
                    mContaFormReceita.error = "Este campo deve ser preenchido"
                    mContaFormDespesa.error = "Este campo deve ser preenchido"
                    return
                }

                if (mContaId.isEmpty()) {
                    if (receita == true) {
                        val cid = mDatabase.reference.child("users/${mAuth.uid}/receitas").push().key
                        val conta = Conta(cid!!, name, valor, createAt)

                        val ref = mDatabase.getReference("users/${mAuth.uid}/receitas/$cid")
                        ref.setValue(conta)

                        handler.post {
                            Toast.makeText(
                                applicationContext,
                                "Receita ${conta.name} adicionada com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val cid = mDatabase.reference.child("users/${mAuth.uid}/despesas").push().key
                        val conta = Conta(cid!!, name, valor, createAt)

                        val ref = mDatabase.getReference("users/${mAuth.uid}/despesas/$cid")
                        ref.setValue(conta)

                        handler.post {
                            Toast.makeText(
                                applicationContext,
                                "Despesa ${conta.name} adicionada com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    finish()
                } else {
                    val conta = Conta(mContaId, name, valor, mCreateAt)


                    if (despesa == true) {

                        val ref = mDatabase.getReference("users/${mAuth.uid}/despesas/$mContaId")

                        ref.setValue(conta).addOnCompleteListener {
                            if (it.isSuccessful) {
                                handler.post {
                                    Toast.makeText(
                                        applicationContext,
                                        "Conta ${conta.name} editada com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                finish()
                            }
                        }
                    } else {
                        val ref = mDatabase.getReference("users/${mAuth.uid}/receitas/$mContaId")

                        ref.setValue(conta).addOnCompleteListener {
                            if (it.isSuccessful) {
                                handler.post {
                                    Toast.makeText(
                                        applicationContext,
                                        "Conta ${conta.name} editada com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                finish()
                            }
                        }
                    }
                }
            }
        }
    }













}