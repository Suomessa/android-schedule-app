package com.hfad.retro

import android.content.SharedPreferences
import android.graphics.Color
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.retro.model.Faculty
import com.hfad.retro.model.Group
import com.hfad.retro.model.Lesson
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "http://192.168.1.47:8082/"

const val GROUP = "groupNumber"
const val FACULTY = "faculty"

class MainActivity : AppCompatActivity() {

    private lateinit var myAdapter: MyAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var retrofitClient: ApiInterface

    var currentDay: Int = setDayOfWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        setContentView(R.layout.activity_main)
        retrofitClient = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        recyclerview_users.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_users.layoutManager = linearLayoutManager
        toggleButtons(false)

        //loadGroup()

        getFaculties()



        button.setOnClickListener {
            toggleButtons(false)

            hideKeyboard()

            for (i in 1..7) setColorToUnselectedButton(i)

            getData()
        }
    }

    fun saveGroup(faculties: List<Faculty>) {
        val sPref = getPreferences(MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sPref.edit()
        ed.putString(GROUP, edit_text_group.selectedItem.toString())
        ed.putString(FACULTY, spinnerFacultyToPath(faculty_spinner.selectedItem.toString(), faculties))
        ed.apply()
    }

    private fun loadGroup() {
        val sPref = getPreferences(MODE_PRIVATE)
        val faculty = sPref.getString(FACULTY, "")
        val group = sPref.getString(GROUP, "")
        if (((faculty != null) && (faculty != "")) && ((group != null) && (group != ""))) {
            getData(faculty, group)
        }
    }

    private fun getFaculties() {
        val retrofitData = retrofitClient.allFaculties()

        retrofitData.enqueue(object : Callback<List<Faculty>?> {
            override fun onResponse(
                call: Call<List<Faculty>?>,
                response: Response<List<Faculty>?>
            ) {
                val responseBody = response.body()!!

                var facultyString = mutableListOf<String>();

                responseBody.forEach { e -> facultyString.add(e.name) }

                var adapter: ArrayAdapter<String> = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, facultyString)

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                var spinner: Spinner = findViewById(R.id.faculty_spinner)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        getGroups(spinner.selectedItem.toString(), responseBody)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }

            }

            override fun onFailure(call: Call<List<Faculty>?>, t: Throwable) {
                d("Main", "UPAL" + t.message)
            }
        })

    }


    private fun getGroups(faculty: String, faculties: List<Faculty>) {
        val retrofitData = retrofitClient.allGroupsOnFaculty(spinnerFacultyToPath(faculty, faculties))

        retrofitData.enqueue(object : Callback<List<Group>?> {
            override fun onResponse(call: Call<List<Group>?>, response: Response<List<Group>?>) {
                val responseBody = response.body()!!

                var groupString = mutableListOf<String>();

                responseBody.forEach { e -> groupString.add(e.groupName) }

                var adapter: ArrayAdapter<String> = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, groupString)

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                var spinner: Spinner = findViewById(R.id.edit_text_group)
                spinner.adapter = adapter
            }

            override fun onFailure(call: Call<List<Group>?>, t: Throwable) {
                d("Main", "UPAL" + t.message)
            }
        })
    }

    private fun getData(faculty: String, group: String) {

        val retrofitData = retrofitClient.getLessons(faculty, group)

        val faculties = retrofitClient.allFaculties()

        faculties.enqueue(object : Callback<List<Faculty>?> {
            override fun onResponse(
                call: Call<List<Faculty>?>,
                response: Response<List<Faculty>?>
            ) {

                val faculties = response.body()!!


                retrofitData.enqueue(object : Callback<List<Lesson>?> {
                    override fun onResponse(
                        call: Call<List<Lesson>?>,
                        response: Response<List<Lesson>?>
                    ) {
                        if (response.body()?.isEmpty() == true) toast("Schedule is empty")
                        else {
                            val responseBody = response.body()!!

                            setButtons(responseBody)

                            toggleButtons(true)

                            saveGroup(faculties)

                            setData(currentDay, responseBody)
                        }
                    }

                    override fun onFailure(call: Call<List<Lesson>?>, t: Throwable) {
                        d("Main", "UPAL" + t.message)
                    }
                })
            }

            override fun onFailure(call: Call<List<Faculty>?>, t: Throwable) {
                d("Main", "UPAL" + t.message)
            }
        })


    }

    private fun getData() {
        if (faculty_spinner.selectedItem.toString() == "No faculty") {
            toast("Select faculty")
        } else {

            val faculties = retrofitClient.allFaculties()

            faculties.enqueue(object : Callback<List<Faculty>> {
                override fun onResponse(
                    call: Call<List<Faculty>>,
                    response: Response<List<Faculty>>
                ) {

                    val facultyList = response.body()!!

                    val retrofitData = retrofitClient.getLessons(
                        spinnerFacultyToPath(faculty_spinner.selectedItem.toString(), facultyList),
                        edit_text_group.selectedItem.toString()
                    )

                    retrofitData.enqueue(object : Callback<List<Lesson>?> {
                        override fun onResponse(
                            call: Call<List<Lesson>?>,
                            response: Response<List<Lesson>?>
                        ) {


                            if (response.body()?.isEmpty() == true) toast("Schedule is empty")
                            else {
                                val responseBody = response.body()!!

                                setButtons(responseBody)

                                toggleButtons(true)

                                saveGroup(facultyList)

                                setData(currentDay, responseBody)
                            }
                        }

                        override fun onFailure(call: Call<List<Lesson>?>, t: Throwable) {
                            d("Main", "UPAL" + t.message)
                        }
                    })
                }

                override fun onFailure(call: Call<List<Faculty>>, t: Throwable) {
                    TODO("Not yet implemented")
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