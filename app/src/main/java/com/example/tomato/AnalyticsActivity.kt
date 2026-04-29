package com.example.tomato

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tomato.util.DatabaseUtil

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var db: DatabaseUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        db = DatabaseUtil(this)


        val todayTomatoCount = getTodayTomatoCount()
        val todayDuration = getTodayDuration()
        val monthlyDuration = getMonthlyDuration()

        val tomato = db.getTomato()

        // 更新 UI 元件
        findViewById<TextView>(R.id.today_tomato_count).text = "今日番茄鐘數量：$todayTomatoCount"
        findViewById<TextView>(R.id.today_duration).text = "$todayDuration 分鐘"
        findViewById<TextView>(R.id.monthly_duration).text = "$monthlyDuration 分鐘"
        findViewById<TextView>(R.id.study_count).text = "${tomato.studyCount} 次"
        findViewById<TextView>(R.id.work_count).text = "${tomato.workCount} 次"
        findViewById<TextView>(R.id.rest_count).text = "${tomato.restCount} 次"
    }


    private fun getTodayTomatoCount(): Int = db.getTomato().oneDayTomato
    private fun getTodayDuration(): Int = (db.getTomato().oneDayTime / 60 / 1000).toInt()
    private fun getMonthlyDuration(): Int = (db.getTomato().monthDayTime  / 60 / 1000).toInt()
}
