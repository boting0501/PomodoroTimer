 🍅 番茄鐘 (PomodoroTimer Timer) 
。基於番茄工作法（Pomodoro Technique），幫助使用者管理時間，並透過數據統計追蹤每日/每月的專注歷程

 📱 專案截圖
 <img width="1460" height="762" alt="螢幕擷取畫面 2026-03-22 032647" src="https://github.com/user-attachments/assets/2015b8c9-7e29-417a-b299-b2a3576369e9" />


✨ 核心功能 (Features)

* **自訂專注與休息時間**：支援客製化分鐘數與秒數，靈活應對不同工作節奏。
* **分類追蹤**：提供「工作」、「學習」、「其他」等標籤，精準紀錄時間花費去向。
* **跨日自動重置邏輯**：系統會自動判斷日期換日，重置「今日」的番茄鐘次數與時間，確保數據準確。
* **數據統計與成就系統**：紀錄單日與月度的專注次數、累積時長，並追蹤連續登入天數。
  
🛠️ 技術棧與架構 
本專案採用 Android 官方推薦的 **Kotlin** 語言進行原生開發，並著重於基礎底層機制的實作與理解：
* **語言**：Kotlin
* **UI 元件**：XML 佈局、Material Design Components (`TextInputLayout`)
* **核心邏輯**：`CountDownTimer` (計時器實作)、`java.time` API (時間與跨日邏輯處理)
* **資料持久化 (Data Persistence)**：
  * 使用原生的 **SQLite (`SQLiteOpenHelper`)** 建構關聯式資料庫。
  * 實作 `CRUD` 操作，並妥善處理 `Cursor` 與連線資源的釋放 (`.use` 區塊)，避免 Memory Leak。
  * 將時間戳轉化為 Epoch Time 儲存，提升跨時區的資料穩定性。

📥 如何執行此專案

1. 將此專案 Clone 到本地端：`git clone https://github.com/boting0501/PomodoroTimer.git`
2. 使用 **Android Studio*開啟專案。
3. 連結實體 Android 裝置或啟動模擬器。
4. 點擊 `Run` 編譯並執行 App。
