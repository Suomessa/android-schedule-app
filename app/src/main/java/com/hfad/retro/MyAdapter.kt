package com.hfad.retro

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_items.view.*


class MyAdapter(val context: Context, val userList: List<Lesson>):
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var number: TextView = itemView.number
        var lessonName: TextView = itemView.lessonName
        var others: TextView = itemView.others
        var teacher: TextView = itemView.teacher
        var room: TextView = itemView.room

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.row_items, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var lesson = userList[position]
        holder.number.text = lesson.number.toString() + " ПАРА: " + getLessonTime(userList[position].number)
        holder.lessonName.text = lesson.name.toString()
        holder.others.text = formatOthers(lesson.parity, lesson.other, lesson.type)
        holder.teacher.text = lesson.teacherName.toString()
        holder.room.text = lesson.room.toString()
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}