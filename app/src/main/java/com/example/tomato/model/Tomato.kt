package com.example.tomato.model

import java.time.LocalDateTime

/**
 * 番茄鐘資料模型
 */
data class Tomato(
    var id: Int, // 唯一識別符，不可重複
    var oneDayTomato: Int, // 單日累計次數
    var oneDayTime: Long, // 單日累積時間，以毫秒做為時間戳
    var monthDayTomato: Int, // 月度累計次數
    var monthDayTime: Long, // 月度累積時間，以毫秒做為時間戳
    var continueDays: Int, // 連續天數
    var studyCount: Int, // 讀書次數
    var workCount: Int, // 工作次數
    var otherCount: Int, // 其他次數
    var restCount: Int, // 休息次數
    var lastUsedDateTime: LocalDateTime? // 最後使用番茄鐘時間
)
