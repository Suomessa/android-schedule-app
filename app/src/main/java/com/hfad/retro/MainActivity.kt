package com.hfad.retro

import android.app.Activity
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "https://schedule-to-ssu.herokuapp.com"
class MainActivity : AppCompatActivity() {

    private lateinit var myAdapter: MyAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    @RequiresApi(Build.VERSION_CODES.N)
    var currentDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        setContentView(R.layout.activity_main)

        recyclerview_users.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_users.layoutManager = linearLayoutManager
        toggleButtons(false)

        button.setOnClickListener {
            toggleButtons(false)
            recyclerview_users.removeAllViews()
            recyclerview_users.removeAllViewsInLayout()

            hideKeyboard()

            for(i in 1..7) setColorToUnselectedButton(i)

            getData()
        }


    }



    private fun getData() {
        if(faculty_spinner.selectedItem.toString() == "No faculty") {
            toast("Select faculty")
        }

         else {
            val retrofitBuilder =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()
                    .create(ApiInterface::class.java)

            val retrofitData = retrofitBuilder.getLessons(spinnerItemToFaculty(faculty_spinner.selectedItem.toString()), edit_text_group.text.toString())

            retrofitData.enqueue(object : Callback<List<Lesson>?> {
                override fun onResponse(
                    call: Call<List<Lesson>?>,
                    response: Response<List<Lesson>?>
                ) {
                    println(response.raw())

                    if(response.body()?.isEmpty() == true) toast("Choose correct group")

                    else {
                        val responseBody = response.body()!!

                        setData(currentDay, responseBody)

                        setButtons(responseBody)

                        toggleButtons(true)

                        setData(currentDay, responseBody)
                    }
                }

                override fun onFailure(call: Call<List<Lesson>?>, t: Throwable) {
                    d("Main", "UPAL" + t.message)
                }
            })
        }
    }

    private fun setButtons(lessons: List<Lesson>) {

        findViewById<Button>(R.id.pn).setOnClickListener {
            setData(1, lessons)
        }

        findViewById<Button>(R.id.vt).setOnClickListener {
            setData(2, lessons)
        }

        findViewById<Button>(R.id.sr).setOnClickListener {
            setData(3, lessons)
        }

        findViewById<Button>(R.id.cht).setOnClickListener {
            setData(4, lessons)
        }

        findViewById<Button>(R.id.pt).setOnClickListener {
            setData(5, lessons)
        }

        findViewById<Button>(R.id.sb).setOnClickListener {
            setData(6, lessons)
        }
    }

    private fun setData(day: Int, lessons: List<Lesson>) {
        recyclerview_users.removeAllViews()

        setColorToUnselectedButton(currentDay)

        currentDay = day

        setColorToSelectedButton(currentDay)

        myAdapter = MyAdapter(baseContext, getLessonsByDay(day, lessons))
        myAdapter.notifyDataSetChanged()
        recyclerview_users.adapter = myAdapter
    }

    private fun setColorToSelectedButton(day: Int) {

        val back = Color.WHITE
        val text = Color.BLACK

        when (day) {
            1 -> {
                val button = findViewById<Button>(R.id.pn)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            2 -> {
                val button = findViewById<Button>(R.id.vt)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            3 -> {
                val button = findViewById<Button>(R.id.sr)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            4 -> {
                val button = findViewById<Button>(R.id.cht)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            5 -> {
                val button = findViewById<Button>(R.id.pt)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            6 -> {
                val button = findViewById<Button>(R.id.sb)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
        }
    }
    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun setColorToUnselectedButton(day: Int) {
        val back = Color.BLACK
        val text = Color.WHITE

        when (day) {
            1 -> {
                val button = findViewById<Button>(R.id.pn)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            2 -> {
                val button = findViewById<Button>(R.id.vt)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            3 -> {
                val button = findViewById<Button>(R.id.sr)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            4 -> {
                val button = findViewById<Button>(R.id.cht)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            5 -> {
                val button = findViewById<Button>(R.id.pt)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
            6 -> {
                val button = findViewById<Button>(R.id.sb)
                button.setBackgroundColor(back)
                button.setTextColor(text)
            }
        }
    }

    private fun toggleButtons(state: Boolean) {
        findViewById<Button>(R.id.pn).isEnabled = state

        findViewById<Button>(R.id.vt).isEnabled = state

        findViewById<Button>(R.id.sr).isEnabled = state

        findViewById<Button>(R.id.cht).isEnabled = state

        findViewById<Button>(R.id.pt).isEnabled = state

        findViewById<Button>(R.id.sb).isEnabled = state
    }
}