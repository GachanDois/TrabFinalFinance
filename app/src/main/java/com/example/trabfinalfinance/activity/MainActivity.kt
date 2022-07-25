package com.example.trabfinalfinance.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabfinalfinance.R
import com.example.trabfinalfinance.adapter.ContaItemListener
import com.example.trabfinalfinance.adapter.DespesaAdapter
import com.example.trabfinalfinance.adapter.ReceitaAdapter
import com.example.trabfinalfinance.model.Conta
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), View.OnClickListener, ContaItemListener {

    private lateinit var mRecyclerViewDesp: RecyclerView
    private lateinit var mRecyclerViewRec: RecyclerView
    private lateinit var mAddConta: FloatingActionButton

    private lateinit var despesaAdapter: DespesaAdapter
    private lateinit var contaReceitaAdapter: ReceitaAdapter

    private val handler = Handler(Looper.getMainLooper())

    private val mDespesaList = mutableListOf<Conta>()
    private val mReceitaList = mutableListOf<Conta>()

    private var mUserId = -1

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDataBase = FirebaseDatabase.getInstance()

        mAuth.currentUser

        mRecyclerViewDesp = findViewById(R.id.main_recview_desp)
        mRecyclerViewRec = findViewById(R.id.main_recview_rec)

        mAddConta = findViewById(R.id.main_floatbutton_add_recdesp)
        mAddConta.setOnClickListener(this)

        if (intent != null) {
            mUserId = intent.getIntExtra("userId", -1)

        }

    }

    override fun onStart() {
        super.onStart()

        val progress = ProgressDialog(this)
        progress.setTitle("To Do List - Aguarde")
        progress.isIndeterminate = true
        progress.setMessage("Carregando os dados...")
        progress.setCancelable(false)

        progress.show()

        val query = mDataBase.reference.child("users/${mAuth.uid}/despesas").orderByKey()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                mDespesaList.clear()

                snapshot.children.forEach{
                    val conta = it.getValue(Conta::class.java)
                    mDespesaList.add(conta!!)
                }
                mDespesaList.sortByDescending {
                    it.createAt
                }

                if (mDespesaList.size > 5) {
                    for (i in (mDespesaList.size - 1) downTo 5)
                        mDespesaList.removeAt(i)

                }

                progress.dismiss()

                despesaAdapter = DespesaAdapter(mDespesaList)
                despesaAdapter.setContaItemListener(this@MainActivity)

                val llm = LinearLayoutManager(applicationContext)

                handler.post {
                    mRecyclerViewDesp.apply {
                        adapter = despesaAdapter
                        layoutManager = llm
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val query2 = mDataBase.reference.child("users/${mAuth.uid}/receitas").orderByKey()
        query2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                mReceitaList.clear()

                snapshot.children.forEach{
                    val conta = it.getValue(Conta::class.java)
                    mReceitaList.add(conta!!)
                }
                mReceitaList.sortByDescending {
                    it.createAt
                }

                if (mReceitaList.size > 5) {
                    for (i in (mReceitaList.size - 1) downTo 5)
                        mReceitaList.removeAt(i)

                }

                progress.dismiss()

                contaReceitaAdapter = ReceitaAdapter(mReceitaList)
                contaReceitaAdapter.setContaItemListener(this@MainActivity)

                val llm = LinearLayoutManager(applicationContext)

                handler.post {
                    mRecyclerViewRec.apply {
                        adapter = contaReceitaAdapter
                        layoutManager = llm
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.main_floatbutton_add_recdesp -> {
                val it = Intent(applicationContext, RecDespAct::class.java)
                startActivity(it)
            }

        }
    }

    override fun onContaItemClick(v: View, pos: Int) {
        val it = Intent(applicationContext, RecDespAct::class.java)
        when (v?.id) {
            R.id.item_despesa_cardview_tipo -> {
                it.putExtra("contaId", mDespesaList[pos].cid)
                it.putExtra("tipo", "despesa")
                startActivity(it)
            }
            R.id.item_receita_cardview_tipo -> {
                it.putExtra("contaId", mReceitaList[pos].cid)
                it.putExtra("tipo", "receita")
                startActivity(it)
            }
        }

    }

    override fun onContaItemLongClick(v: View, pos: Int) {
        when (v?.id) {
            R.id.item_despesa_cardview_tipo -> {
                val despesa = mDespesaList[pos]

                val alert = AlertDialog.Builder(this)
                    .setTitle("To Do List")
                    .setMessage("Você deseja excluir a tarefa ${despesa.name}?")
                    .setPositiveButton("Sim") {dialog, _ ->
                        dialog.dismiss()

                        val ref = mDataBase.reference.child("users/${mAuth.uid}/despesas/${mDespesaList[pos].cid}")
                        ref.removeValue().addOnCompleteListener{
                            handler.post {
                                despesaAdapter.notifyItemRemoved(pos)
                                despesaAdapter.notifyDataSetChanged()

                            }
                        }


                    }
                    .setNegativeButton("Não") {dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()

                alert.show()
            }
            R.id.item_receita_cardview_tipo -> {
                val receita = mReceitaList[pos]

                val alert = AlertDialog.Builder(this)
                    .setTitle("To Do List")
                    .setMessage("Você deseja excluir a tarefa ${receita.name}?")
                    .setPositiveButton("Sim") {dialog, _ ->
                        dialog.dismiss()

                        val ref = mDataBase.reference.child("users/${mAuth.uid}/receitas/${mReceitaList[pos].cid}")
                        ref.removeValue().addOnCompleteListener{
                            handler.post {
                                contaReceitaAdapter.notifyItemRemoved(pos)
                                contaReceitaAdapter.notifyDataSetChanged()

                            }
                        }


                    }
                    .setNegativeButton("Não") {dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()


                alert.show()
            }
        }
    }

}