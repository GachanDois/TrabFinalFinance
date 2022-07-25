package com.example.trabfinalfinance.adapter

import android.view.View

interface ContaItemListener {

    fun onContaItemClick(v: View, pos: Int)

    fun onContaItemLongClick(v: View, pos: Int)
}