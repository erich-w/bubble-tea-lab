# OOP 手搖飲 🧋 Java 實作課

今天你會完成一個手搖飲點餐程式，然後把它串接到真正的 AI 模型（Google 免費的 Gemma）。

## 0. 下載與設定（只要做一次）

1. 在這個 GitHub 頁面按綠色的 **Code → Download ZIP**。
2. 在下載的 ZIP 上按右鍵 → **解壓縮全部**。⚠️ 一定要先解壓縮，不能在 ZIP 裡面直接執行。
3. 打開解壓縮後的資料夾，雙擊 **`setup.bat`**。
   - 如果 Windows 跳出安全性警告，請按「執行」（或「其他資訊 → 仍要執行」）。
4. 它要求金鑰時，到 <https://aistudio.google.com/apikey> 複製你的 API 金鑰，在視窗裡按**滑鼠右鍵**貼上，再按 Enter。
5. 看到「全部正常，可以開始上課了！」就完成了。

有任何錯誤，請舉手，把視窗給助教看。

## 1. 用 VS Code 打開專案

`File → Open Folder…`（檔案 → 開啟資料夾）→ 選擇剛剛解壓縮的資料夾。
打開終端機：`Terminal → New Terminal`（終端機 → 新增終端機）。

| 指令 | 作用 |
|---|---|
| `.\run.bat check` | 檢查你的程式，顯示 PASS / FAIL |
| `.\run.bat` | 執行手搖飲店 |
| `.\run.bat setup` | 再測試一次 API 金鑰 |

⚠️ 前面的 `.\` 不能省略。（Mac/Linux 請改用 `./run.sh`。）

## 2. 專案架構

```
          Drink（抽象類別）                    DrinkRecommender（介面）
           /         \                          /                  \
      MilkTea      FruitTea ← 關卡1   RuleBasedRecommender     LLMRecommender ← 關卡3
       奶茶          水果茶              用 if/else 規則               |
                                                               LLMClient（介面）
   Order ← 關卡2（一筆訂單有很多杯 Drink）                             |
                                                              GeminiClient → Google API
```

## 3. 你的任務

| # | 檔案 | 要做什麼 |
|---|---|---|
| 關卡 1 | `FruitTea.java` | 回傳名字和價格，參考 `MilkTea.java` 的寫法 |
| 關卡 2 | `Order.java` | 完成 `addDrink()` 和 `getTotal()` |
| 關卡 3 | `LLMRecommender.java` | 組出 prompt，用 `client.generate()` 送給 AI |
| 步驟 4 | `Main.java` | 把 `RuleBasedRecommender` 換成 `LLMRecommender`（只改一行！），再執行 `.\run.bat` |

每完成一關就執行一次 `.\run.bat check`，**先修第一個 FAIL**。

**提早做完？** 自己從零寫一個新飲料類別 `GreenTea`（綠茶，NT$40），把它加進 `Menu.java`，再問 AI 推薦，看看它會不會推薦你的新飲料。

## 4. 🔐 API 金鑰安全守則

- 金鑰放在 `api_key.properties`，**不是**寫在程式碼裡。
- **絕對不要**把金鑰貼在程式碼、截圖、LINE 群組或 GitHub 上。拿到你金鑰的人可以用掉你的額度。
- `api_key.properties` 已經列在 `.gitignore`，git 不會上傳它。
- 金鑰外流了怎麼辦？到 AI Studio → API keys 刪除它，再建立一組新的。
- 免費方案的對話內容可能被 Google 用來改進模型，請不要輸入個人資料。
