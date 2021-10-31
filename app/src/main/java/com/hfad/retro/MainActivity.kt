package com.hfad.retro

import android.content.SharedPreferences
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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


const val BASE_URL = "https://schedule-to-ssu.herokuapp.com/"

const val GROUP = "groupNumber"
const val FACULTY = "faculty"

class MainActivity : AppCompatActivity() {

    private lateinit var myAdapter: MyAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    var currentDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        setContentView(R.layout.activity_main)

        recyclerview_users.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_users.layoutManager = linearLayoutManager
        toggleButtons(false)

        loadGroup()

        button.setOnClickListener {
            toggleButtons(false)

            hideKeyboard()

            for (i in 1..7) setColorToUnselectedButton(i)

            getData()
        }
    }

    fun saveGroup() {
        val sPref = getPreferences(MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString(GROUP, edit_text_group.text.toString())
        ed.putString(FACULTY, spinnerItemToFaculty(faculty_spinner.selectedItem.toString()))
        ed.apply()
    }

    private fun loadGroup() {
        val sPref = getPreferences(MODE_PRIVATE)
        val faculty = sPref.getString(FACULTY, "")
        val group = sPref.getString(GROUP, "")
        if (((faculty != null) && (faculty != "")) && ((group != null) && (group != ""))) {
            edit_text_group.setText(group)
            getData(faculty, group)
        }
    }

    private fun getData(faculty: String, group: String) {

        val retrofitBuilder =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getLessons(faculty, group)

        retrofitData.enqueue(object : Callback<List<Lesson>?> {
            override fun onResponse(
                call: Call<List<Lesson>?>,
                response: Response<List<Lesson>?>
            ) {
                if (response.body()?.isEmpty() == true) toast("Choose correct group")
                else {
                    val responseBody = response.body()!!

                    setButtons(responseBody)

                    toggleButtons(true)

                    saveGroup()

                    setData(currentDay, responseBody)
                }
            }

            override fun onFailure(call: Call<List<Lesson>?>, t: Throwable) {
                d("Main", "UPAL" + t.message)
            }
        })
    }

    private fun getData() {
        if (faculty_spinner.selectedItem.toString() == "No faculty") {
            toast("Select faculty")
        } else {
            val retrofitBuilder =
                Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()
                    .create(ApiInterface::class.java)

            val retrofitData = retrofitBuilder.getLessons(
                spinnerItemToFaculty(faculty_spinner.selectedItem.toString()),
                edit_text_group.text.toString()
            )

            retrofitData.enqueue(object : Callback<List<Lesson>?> {
                override fun onResponse(
                    call: Call<List<Lesson>?>,
                    response: Response<List<Lesson>?>
                ) {


                    if (response.body()?.isEmpty() == true) toast("Choose correct group")
                    else {
                        val responseBody = response.body()!!

                        setButtons(responseBody)

                        toggleButtons(true)

                        saveGroup()

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

        val lessonsList = getLessonsByDay(day, lessons)

        val info: LinearLayout = findViewById(R.id.info)

        info.removeAllViews()

        info.addView(newDayName(day))

        if (lessonsList.isEmpty()) {
            info.addView(noLessonsInfo())
        }

        myAdapter = MyAdapter(baseContext, lessonsList)
        myAdapter.notifyDataSetChanged()
        recyclerview_users.adapter = myAdapter
    }

    private fun newDayName(day: Int): TextView {
        val textView = TextView(this)
        textView.text = dayChooser(day)
        textView.textSize = 25f
        textView.gravity = Gravity.CENTER_HORIZONTAL
        return textView
    }

    private fun noLessonsInfo(): TextView {
        val textView = TextView(this)
        textView.text = "На сегодня пар нет!"
        textView.textSize = 20f
        textView.gravity = Gravity.CENTER_HORIZONTAL
        return textView
    }

    private fun dayChooser(day: Int): String {
        when (day) {
            1 -> {
                return "ПОНЕДЕЛЬНИК"
            }
            2 -> {
                return "ВТОРНИК"
            }
            3 -> {
                return "СРЕДА"
            }
            4 -> {
                return "ЧЕТВЕРГ"
            }
            5 -> {
                return "ПЯТНИЦА"
            }
            6 -> {
                return "СУББОТА"
            }
            7 -> {
                return "Воскресенье"
            }
        }
        return "Error"
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