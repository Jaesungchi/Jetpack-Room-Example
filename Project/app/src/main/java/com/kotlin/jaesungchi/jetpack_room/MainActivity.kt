package com.kotlin.jaesungchi.jetpack_room

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    lateinit var appDatabase : AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appDatabase = AppDatabase.getInstance(this)!!
        insertBtn.setOnClickListener{
            insertData()
        }
        initView()
    }

    private fun initView(){
        var tmps : Array<WriteDataEntity>? = null
        CoroutineScope(Dispatchers.IO).launch {
            tmps = appDatabase.WriteDao()?.selectAll()
        }
        CoroutineScope(Dispatchers.Main).launch {
            if (tmps != null)
                for(i in tmps!!)
                    updateView(i)
        }
    }

    private fun insertData(){
        if(titleText.text.isNullOrEmpty() || contentText.text.isNullOrEmpty())
            return
        var insertTmp =WriteDataEntity(0,titleText.text.toString(), contentText.text.toString(), LocalDate.now().toString() +" "+ LocalTime.now().toString().subSequence(0,8))
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.WriteDao()?.insert(insertTmp)
        }
        updateView(insertTmp)
        titleText.text = null
        contentText.text = null
        closeKeyboard()
    }

    private fun updateView(insertData : WriteDataEntity){
        //materLayout에 넣을 레이아웃 생성.
        var addLayout = LinearLayout(this@MainActivity)
        addLayout.orientation = LinearLayout.HORIZONTAL
        addLayout.layoutParams = LinearLayout.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        var textTmp = TextView(this@MainActivity)
        textTmp.text = "${insertData.title}  ${insertData.date}\n ${insertData.content}\n"
        textTmp.layoutParams = LinearLayout.LayoutParams(1,WindowManager.LayoutParams.WRAP_CONTENT)
        (textTmp.layoutParams as LinearLayout.LayoutParams).weight = .8f
        (textTmp.layoutParams as LinearLayout.LayoutParams).setMargins(60,5,5,5)
        addLayout.addView(textTmp)
        var deleteTmp = Button(this@MainActivity)
        deleteTmp.text = "삭제"
        deleteTmp.layoutParams = LinearLayout.LayoutParams(1,WindowManager.LayoutParams.FILL_PARENT)
        (deleteTmp.layoutParams as LinearLayout.LayoutParams).weight = .2f
        deleteTmp.setPadding(5,5,5,5)
        deleteTmp.setOnClickListener {
            deleteData(insertData.id)
            masterLayout.removeView(addLayout)
        }
        addLayout.addView(deleteTmp)
        masterLayout.addView(addLayout)
    }

    private fun deleteData(id : Long){
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.WriteDao()?.deleteById(id)
        }
    }

    private fun closeKeyboard(){
        var view = this.currentFocus
        if(view != null){
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}
