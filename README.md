# StockAPIDemo

使用臺灣證券交易所 OpenAPI 的 Android 面試作品，以 Kotlin、XML DataBinding 與 MVVM 實作三種股票資料列表。

## 功能

- 自訂橫向 RecyclerView 分頁列，搭配 ViewPager2 切換三個 Fragment。
- 估值指標：股票代號、名稱與本益比，展開查看殖利率、股價淨值比。
- 收盤均價：直接顯示代號、名稱、收盤價與月平均價。
- 每日成交：收盤價與漲跌價差，展開查看開高低價、成交股數、金額與筆數。
- 固定欄位表頭與資料列共用欄位 layout，保持對齊。
- AppBarLayout 隨列表滑動收合標題，保留分頁列。
- 右下角搜尋按鈕向左展開成輸入泡泡；輸入停頓 300ms 後依名稱或代號部分比對，切頁保留條件。
- 載入提示、錯誤重試、空資料與查無搜尋結果提示。
- 深淺色資源；搜尋文字與開關狀態在畫面重建時保存。

## 技術與架構

Kotlin / XML / DataBinding / MVVM / LiveData / RxJava 3 Flowable / Retrofit / OkHttp / Gson。
列表使用 BRVAH 4.1.4 官方 DataBindingHolder，折疊使用 ExpandableLayout 2.9.2。

```text
MainActivity                 分頁、共用搜尋文字與系統列處理
view/                        三個 Fragment、搜尋泡泡 UI 與動畫
viewmodel/                   各頁原始資料、搜尋、載入與錯誤處理
repository/                  三支 API 的資料入口
network/                     Retrofit、OkHttp 與 TwseApi
model/DataClasses.kt         API 資料與分頁 data class
model/StockSearch.kt         共用名稱／代號篩選函式
adapter/                     BRVAH 資料綁定
```

Repository 經 RxJava 將資料傳入 ViewModel，以 MutableLiveData 更新，對外提供 LiveData 供 Fragment 觀察。網路請求在 IO 執行，畫面更新切回主執行緒，ViewModel 清除時釋放訂閱。

代號保留字串以保留前導零；缺值顯示為「—」，不當作 0。成交數值使用 BigDecimal 格式化。搜尋在已載入的原始資料上執行，不會每次輸入就重新請求 API，也不會覆蓋原始清單。

## 資料來源

[臺灣證券交易所 OpenAPI](https://openapi.twse.com.tw/)，Base URL：`https://openapi.twse.com.tw/v1/`。

| API | 畫面 |
| --- | --- |
| `/exchangeReport/BWIBBU_ALL` | 估值指標 |
| `/exchangeReport/STOCK_DAY_AVG_ALL` | 收盤均價 |
| `/exchangeReport/STOCK_DAY_ALL` | 每日成交 |

畫面日期依 API 回傳資料顯示，並非即時報價。

## 開啟與建置

1. Clone 專案，以支援本專案 AGP 9.3.2 的 Android Studio 開啟。
2. 安裝 Android SDK 36，Gradle JDK 設為 JDK 21；可使用相容的 Android Studio 內建 JBR。
3. 由 Android Studio 建立本機 `local.properties`，完成 Gradle Sync。
4. 選取 `app`，在 Android 7.0（API 24）以上手機或模擬器執行。

專案使用 Gradle Wrapper，compileSdk / targetSdk 為 36。首次同步需要網路下載依賴。

Windows PowerShell（先設定有效的 JAVA_HOME）：

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug
```

APK 輸出：`app/build/outputs/apk/debug/app-debug.apk`。

## 測試

不依賴網路的搜尋單元測試，涵蓋名稱、代號、前導零、大小寫、空白、缺值、查無結果與連續搜尋：

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*StockSearchTest'
```

實際 API 整合測試會呼叫三支 API，檢查回傳非空與 Code / Name / Date，並在 Run 視窗或 Gradle console 輸出筆數與前三筆資料，標記為 `[TwseApiTest]`。此測試需要網路，會受外部服務狀態影響。

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*TwseApiLiveTest' -PtwseLiveTest=true --console=plain
```

測試報告：`app/build/reports/tests/testDebugUnitTest/index.html`。

## 展示影片與驗證狀態

YouTube 操作影片連結待補（一般影片、非 Shorts，可見度設為「非公開」）。

搜尋功能已通過編譯、Lint 與 6 項搜尋單元測試；搜尋動畫、鍵盤互動及旋轉後的操作仍待實機確認。專案內的 ExampleInstrumentedTest 是範本測試，不代表完整 UI 測試覆蓋。
