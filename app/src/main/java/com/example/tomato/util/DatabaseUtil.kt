package com.example.tomato.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.CalendarContract.Instances
import androidx.core.database.getIntOrNull
import com.example.tomato.model.Tomato
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone


/**
 * 處理資料庫的工具類別
 */
class DatabaseUtil (
    context: Context?,
) : SQLiteOpenHelper(context, "tomato_v1.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        // 建立資料表
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Tomato(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                one_day_tomato INTEGER NOT NULL,
                one_day_time INTEGER NOT NULL,
                month_day_tomato INTEGER NOT NULL,
                month_day_time INTEGER NOT NULL,
                continue INTEGER NOT NULL,
                study_count INTEGER NOT NULL,
                work_count INTEGER NOT NULL,
                other_count INTEGER NOT NULL,
                rest_count INTEGER NOT NULL,
                last_used_date_time INTEGER
            )
        """.trimIndent())

        // 新增紀錄用資料
        db.execSQL("""
            INSERT INTO Tomato(
                one_day_tomato,
                one_day_time,
                month_day_tomato,
                month_day_time,
                continue,
                study_count,
                work_count,
                other_count,
                rest_count
            ) VALUES (
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    /**
     * 取得番茄時鐘資料
     */
    fun getTomato(): Tomato {
        this.readableDatabase.use {
            // 讀出 Tomato 資料表所有資料列
            val cursor = it.rawQuery("SELECT * FROM Tomato", arrayOf())

            // 因為只有一筆資料，所以只取第一筆就好
            cursor.moveToFirst()

            // 直接把第一筆包成資料模型後回傳
            return Tomato(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getLong(2),
                cursor.getInt(3),
                cursor.getLong(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getInt(8),
                cursor.getInt(9),
                if (cursor.isNull(10)) null else LocalDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(10)), TimeZone.getDefault().toZoneId())
            )
        }
    }

    /**
     * 更新 Tomato 資料
     * @param tomato 要更新的 Tomato 資料，會透過 Tomato.id 在資料庫進行比對
     */
    fun updateTomato(tomato: Tomato) {
        this.writableDatabase.use {
            it.update("Tomato", ContentValues().apply {
                put("one_day_tomato", tomato.oneDayTomato)
                put("one_day_time", tomato.oneDayTime)
                put("month_day_tomato", tomato.monthDayTomato)
                put("month_day_time", tomato.monthDayTime)
                put("continue", tomato.continueDays)
                put("study_count", tomato.studyCount)
                put("work_count", tomato.workCount)
                put("other_count", tomato.otherCount)
                put("rest_count", tomato.restCount)
                put("last_used_date_time", tomato.lastUsedDateTime?.atZone(TimeZone.getDefault().toZoneId())?.toEpochSecond())
            }, "id=?", arrayOf(tomato.id.toString()))
        }
    }
}