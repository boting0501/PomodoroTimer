package com.example.tomato

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.tomato.util.DatabaseUtil
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var currentPhaseText: TextView
    private lateinit var phaseText: TextView
    private lateinit var totalText: TextView
    private lateinit var spinner: Spinner
    private lateinit var spinnerLayout: TextInputLayout
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var minutesInput: TextInputEditText
    private lateinit var secondsInput: TextInputEditText
    private lateinit var db: DatabaseUtil
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var totalTimeInMillis: Long = 25 * 60 * 1000 // 默認25分鐘
    private var remainingTimeInMillis = totalTimeInMillis
    private var isStudyPhase = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 初始化資料庫訪問工具
        db = DatabaseUtil(this)
        // 初始化發茄鐘的次數，在 onCreate 需要呼叫一遍是因為需要確保單日次數、單日時間等資料沒問題
        refreshTomatoCount()

        // 初始化UI元件
        totalText = findViewById(R.id.total_text)
        timerText = findViewById(R.id.timer_text)
        currentPhaseText = findViewById(R.id.current_phase_text)
        phaseText = findViewById(R.id.phase_text)
        spinner = findViewById(R.id.phase_spinner)
        spinnerLayout = findViewById(R.id.spinner_layout)
        startButton = findViewById(R.id.start_button)
        resetButton = findViewById(R.id.reset_button)
        minutesInput = findViewById(R.id.minutes_input)
        secondsInput = findViewById(R.id.seconds_input)

        // 從資料庫抓取單日次數
        totalText.text = "今日累積番茄鐘數:${db.getTomato().oneDayTomato}"

        phaseText.visibility = View.INVISIBLE
        updateTimer() // 初始設置計時器顯示

        // 導航按鈕

        findViewById<Button>(R.id.button_achievements).setOnClickListener {
            startActivity(Intent(this, AchievementActivity::class.java))
        }

        findViewById<Button>(R.id.button_analytics).setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }

        // 邊緣到邊緣視窗插入
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Spinner設置
        val options = resources.getStringArray(R.array.phase_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)

        // 開始/停止按鈕邏輯
        startButton.setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(this@MainActivity, "請選擇階段", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedPhase = spinner.selectedItem.toString()
            phaseText.visibility = View.VISIBLE
            currentPhaseText.text = "目前階段為 $selectedPhase"
            spinner.visibility = View.GONE
            spinnerLayout.visibility = View.GONE

            // 檢查自訂時間輸入
            val customMinutes = minutesInput.text.toString().toIntOrNull() ?: 25
            val customSeconds = secondsInput.text.toString().toIntOrNull() ?: 0

            // 第一次啟動或重新設置時更新總時間和剩餘時間
            if (!isTimerRunning && remainingTimeInMillis == totalTimeInMillis) {
                totalTimeInMillis = ((customMinutes * 60 + customSeconds) * 1000).toLong()
                remainingTimeInMillis = totalTimeInMillis
                updateTimer()
                isStudyPhase = true
            }

            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        // 重置按鈕邏輯
        resetButton.setOnClickListener {
            currentPhaseText.text = ""
            spinnerLayout.visibility = View.VISIBLE
            spinner.visibility = View.VISIBLE
            resetTimer()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                isTimerRunning = false
                startButton.text = "Start"
                startButton.setBackgroundColor(
                    ContextCompat.getColor(this@MainActivity, R.color.green))
                startButton.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.darkgreen))
                switchPhase()
            }
        }.start()

        isTimerRunning = true
        startButton.text = "Pause"
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        startButton.setTextColor(ContextCompat.getColor(this, R.color.darkred))

        // 修正：開始計時時，顯示專注時間或當前階段
        phaseText.text = if (isStudyPhase) "專注時間" else "休息時間"

    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        startButton.text = "Start"
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        startButton.setTextColor(ContextCompat.getColor(this, R.color.darkgreen))
        phaseText.text = "暫停"
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        totalTimeInMillis = 25 * 60 * 1000
        remainingTimeInMillis = totalTimeInMillis
        updateTimer()
        isTimerRunning = false
        isStudyPhase = true
        startButton.text = "Start"
        phaseText.visibility = View.INVISIBLE
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        startButton.setTextColor(ContextCompat.getColor(this, R.color.darkgreen))
        minutesInput.text?.clear()
        secondsInput.text?.clear()
    }

    private fun switchPhase() {
        if (isStudyPhase) {
            // 剛結束專注時間後，重新確認
            refreshTomatoCount()
            completeTomato()
            totalText.text = "今日累積番茄鐘數:${db.getTomato().oneDayTomato}"
        } else {
            val tomato = db.getTomato()
            tomato.restCount++
            db.updateTomato(tomato)
        }

        isStudyPhase = !isStudyPhase

        // 當是專注階段時，使用自訂的時間；否則使用預設5分鐘休息
        totalTimeInMillis = if (isStudyPhase) {
            // 從輸入框獲取自訂時間，若無輸入則預設為用戶上次設定的時間
            val customMinutes = minutesInput.text.toString().toIntOrNull() ?: (totalTimeInMillis / 60 / 1000)
            val customSeconds = secondsInput.text.toString().toIntOrNull() ?: 0
            ((customMinutes.toLong()  * 60 + customSeconds) * 1000).toLong()
        } else {
            5 * 60 * 1000 // 固定5分鐘休息

        }

        remainingTimeInMillis = totalTimeInMillis
        updateTimer()
        updatePhaseText()
        startTimer()
    }

    private fun updateTimer() {
        val minutes = (remainingTimeInMillis / 1000) / 60
        val seconds = (remainingTimeInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerText.text = timeFormatted
    }

    private fun updatePhaseText() {
        val phaseName = if (isStudyPhase) "專注時間" else "休息時間"
        phaseText.text = phaseName
    }

    /**
     * 刷新番茄鐘的計數
     * 需要這麼做的原因是因為，資料庫只有一筆資料。必須確保這一筆資料紀錄的單日、月度、連續天數資料是正確的
     * 主要透過 lastUsedDateTime 去判斷
     */
    private fun refreshTomatoCount() {
        val tomato = db.getTomato()

        if (tomato.lastUsedDateTime == null) {
            tomato.lastUsedDateTime = LocalDateTime.now()
        }

        // 從資料庫拿最後使用時間，因為用不到時間判斷，只用的到日期，所以轉成 LocalDate
        val lastUsedDate = tomato.lastUsedDateTime!!.toLocalDate()

        // 取今天的日期方便後面做判斷
        val nowDate = LocalDate.now()

        // 當最後使用的時間已經過一天後
        // 將單日次數跟時間重置
        if (lastUsedDate < nowDate) {
            tomato.oneDayTomato = 0
            tomato.oneDayTime = 0
        }

        // 當最後使用時間的年跟月對不上現在的年月的話
        // 重置月度次數、月度時長以及累積工作次數等資料
        if (lastUsedDate.year != nowDate.year &&
            lastUsedDate.month != nowDate.month) {
            tomato.monthDayTime = 0
            tomato.monthDayTomato = 0
            tomato.restCount = 0
            tomato.workCount = 0
            tomato.studyCount = 0
            tomato.otherCount = 0
        }

        // 如果最後使用時間是前一天，那就把連續天數加 1
        if (lastUsedDate.plusDays(1) == nowDate) {
            tomato.continueDays++
        } else if (lastUsedDate.plusDays(1) < nowDate) { // 如果最後使用時間以及超過前一天了，那就把連續天數重置
            tomato.continueDays = 0
        }

        db.updateTomato(tomato)
    }

    /**
     * 完成一次番茄鐘
     */
    private fun completeTomato() {
        val tomato = db.getTomato()

        tomato.oneDayTomato++
        tomato.monthDayTomato++
        tomato.oneDayTime += totalTimeInMillis
        tomato.monthDayTime += totalTimeInMillis
        tomato.lastUsedDateTime = LocalDateTime.now()

        when (spinner.selectedItem) {
            "工作" -> tomato.workCount++
            "學習" -> tomato.studyCount++
            "其他" -> tomato.otherCount++
        }

        // 如果連續天數是 0，因為今天是第一次做，所以把連續天數改為 1
        if (tomato.continueDays == 0) {
            tomato.continueDays = 1
        }

        db.updateTomato(tomato)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}