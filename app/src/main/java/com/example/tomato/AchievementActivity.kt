
package com.example.tomato

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tomato.util.DatabaseUtil

class AchievementActivity : AppCompatActivity() {
    private lateinit var db: DatabaseUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        db = DatabaseUtil(this)

        // 模擬數據（替換為實際數據來源）
        val todayTomatoCount = getTodayTomatoCount()
        val todayDuration = getTodayDuration()
        val monthlyDuration = getMonthlyDuration()
        val consecutiveDays = getConsecutiveDays()
        val monthlyTomatoCount = getMonthlyTomatoCount()

        // 獲取所有 CheckedTextView
        val checkedTextViews = listOf(
            findViewById<CheckedTextView>(R.id.checkedTextView7),
            findViewById<CheckedTextView>(R.id.checkedTextView4),
            findViewById<CheckedTextView>(R.id.checkedTextView2),
            findViewById<CheckedTextView>(R.id.checkedTextView),
            findViewById<CheckedTextView>(R.id.checkedTextView5),
            findViewById<CheckedTextView>(R.id.checkedTextView3),
            findViewById<CheckedTextView>(R.id.checkedTextView6)
        )

        // 獲取鼓勵文字的 TextView
        val encouragementTextView = findViewById<TextView>(R.id.textView4)

        // 初始隱藏鼓勵文字
        encouragementTextView.visibility = View.GONE

        // 更新顏色並檢查是否所有成就都達成
        var allAchievementsCompleted = true

        checkedTextViews[0].apply {
            if (todayDuration >= 100) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        checkedTextViews[1].apply {
            if (todayTomatoCount >= 5) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        checkedTextViews[2].apply {
            if (consecutiveDays >= 3) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        checkedTextViews[3].apply {
            if (consecutiveDays >= 7) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        checkedTextViews[4].apply {
            if (monthlyTomatoCount >= 30) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        checkedTextViews[5].apply {
            if (monthlyDuration >= 500) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        checkedTextViews[6].apply {
            if (monthlyDuration >= 800) setTextColor(Color.RED)
            else allAchievementsCompleted = false
        }

        // 如果所有成就都完成，顯示鼓勵文字
        if (allAchievementsCompleted) {
            encouragementTextView.visibility = View.VISIBLE
        }
    }

    // 模擬數據源
    private fun getTodayTomatoCount(): Int = db.getTomato().oneDayTomato // 假設今日 5 次番茄鐘
    private fun getTodayDuration(): Int = (db.getTomato().oneDayTime / 60 / 1000).toInt() // 假設今日 125 分鐘
    private fun getMonthlyDuration(): Int = (db.getTomato().monthDayTime  / 60 / 1000).toInt() // 假設月度 3000 分鐘
    private fun getConsecutiveDays(): Int = db.getTomato().continueDays // 假設連續 5 天
    private fun getMonthlyTomatoCount(): Int = db.getTomato().monthDayTomato // 假設本月 35 次番茄鐘
}