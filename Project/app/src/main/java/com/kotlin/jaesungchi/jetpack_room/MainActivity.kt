package com.kotlin.jaesungchi.jetpack_room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        insertBtn.setOnClickListener{
            insertData()
        }
    }

    private fun insertData(){
        if(titleText.text.isNullOrEmpty() || contentText.text.isNullOrEmpty())
            return
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(this@MainActivity)?.WriteDao()?.insert(
                WriteDataEntity(0,titleText.text.toString(), contentText.text.toString(), LocalDate.now().toString())
            )
        }
        updateView()
    }

    fun updateView(){
        CoroutineScope(Dispatchers.IO).launch {
            var tmps = AppDatabase.getInstance(this@MainActivity)?.WriteDao()?.selectAll()
            if (tmps != null) {
                for(i in tmps)
                    Log.d("test",i.toString())
            }
        }
    }
}
