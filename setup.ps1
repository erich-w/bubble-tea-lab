# ==========================================================
#  OOP 手搖飲 - Windows 環境設定（不需要系統管理員權限）
#  使用方式：雙擊 setup.bat
#
#  1. 檢查 Java 17 以上。沒有的話，安裝攜帶版 JDK 21 到你的使用者資料夾
#     （如果同一個資料夾裡有 jdk.zip 就用它，否則自動下載，約 190 MB）
#  2. 請你貼上 Gemini API 金鑰，存成 api_key.properties
#  3. 編譯專案，並傳一則測試訊息給 AI
# ==========================================================
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12 } catch {}

$root    = Split-Path -Parent $MyInvocation.MyCommand.Path
$jdkDir  = Join-Path $env:LOCALAPPDATA 'bubbletea-jdk'
$jdkUrl  = 'https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse'
$minJava = 17
$model   = 'gemma-4-26b-a4b-it'
$utf8    = New-Object System.Text.UTF8Encoding $false
Set-Location $root

function Step($m) { Write-Host ''; Write-Host "==> $m" -ForegroundColor Cyan }
function Ok($m)   { Write-Host "  [OK] $m" -ForegroundColor Green }
function Bad($m)  { Write-Host "  [X]  $m" -ForegroundColor Red }

function Get-JavacMajor {
    try {
        $out = & cmd /c "javac -version 2>&1"
        if ("$out" -match 'javac (\d+)') { return [int]$Matches[1] }
    } catch {}
    return 0
}

function Find-PortableJdk {
    if (-not (Test-Path $jdkDir)) { return $null }
    Get-ChildItem $jdkDir -Directory |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin\javac.exe') } |
        Select-Object -First 1
}

Write-Host ''
Write-Host '  ===== OOP 手搖飲 環境設定 =====' -ForegroundColor Yellow

# ---------------- 1. Java ----------------
Step '檢查 Java'
$jdkHome = $null
$pathMajor = Get-JavacMajor
if ($pathMajor -ge $minJava) {
    Ok "這台電腦已安裝 Java $pathMajor"
    if (Test-Path (Join-Path $root '.jdk_home')) { Remove-Item (Join-Path $root '.jdk_home') }
} else {
    if ($pathMajor -gt 0) { Write-Host "  Java $pathMajor 版本太舊（需要 $minJava 以上），安裝新版中..." }
    else { Write-Host '  找不到 Java（JDK），安裝攜帶版 JDK 21 中...' }

    $found = Find-PortableJdk
    if ($found) {
        Ok '之前已經安裝過攜帶版 JDK'
    } else {
        $zip = Join-Path $root 'jdk.zip'
        if (Test-Path $zip) {
            Write-Host '  使用專案資料夾裡的 jdk.zip'
        } else {
            $zip = Join-Path $env:TEMP 'bubbletea-jdk.zip'
            Write-Host '  下載 JDK 21 中（約 190 MB，需要 1-3 分鐘）...'
            try {
                Invoke-WebRequest -Uri $jdkUrl -OutFile $zip -UseBasicParsing
            } catch {
                Bad "下載失敗：$($_.Exception.Message)"
                Bad '請跟助教拿 jdk.zip，放在 setup.bat 旁邊，再執行一次 setup.bat。'
                exit 1
            }
        }
        Write-Host '  解壓縮中...'
        New-Item -ItemType Directory -Force -Path $jdkDir | Out-Null
        $tar = Get-Command tar.exe -ErrorAction SilentlyContinue
        if ($tar) { & tar.exe -xf $zip -C $jdkDir } else { Expand-Archive -Path $zip -DestinationPath $jdkDir -Force }
        $found = Find-PortableJdk
    }
    if (-not $found) { Bad '無法安裝 Java，請找助教。'; exit 1 }
    $jdkHome = $found.FullName
    # run.bat 用目前的字碼頁讀這個檔案，所以用系統預設編碼存
    [System.IO.File]::WriteAllText((Join-Path $root '.jdk_home'), $jdkHome, [System.Text.Encoding]::Default)
    Ok "Java 已就緒：$jdkHome"
}

# VS Code 設定（有安裝 Java 擴充套件時才會用到）
$vscode = Join-Path $root '.vscode'
New-Item -ItemType Directory -Force -Path $vscode | Out-Null
$settings = "{`r`n  `"java.project.sourcePaths`": [`"src`"],`r`n  `"java.project.outputPath`": `"bin`",`r`n  `"files.encoding`": `"utf8`""
if ($jdkHome) { $settings += ",`r`n  `"java.jdt.ls.java.home`": `"" + $jdkHome.Replace('\', '\\') + "`"" }
$settings += "`r`n}`r`n"
[System.IO.File]::WriteAllText((Join-Path $vscode 'settings.json'), $settings, $utf8)

# ---------------- 2. API 金鑰 ----------------
Step '設定 API 金鑰'
$keyFile = Join-Path $root 'api_key.properties'
if (Test-Path $keyFile) {
    Ok '已經有 api_key.properties（要換金鑰的話，刪掉它再執行一次 setup）'
} else {
    Write-Host '  1. 打開 https://aistudio.google.com/apikey 複製你的金鑰'
    Write-Host '  2. 貼到下面（在這個視窗按滑鼠右鍵就是貼上），再按 Enter'
    Write-Host '     金鑰只會存在這台電腦的 api_key.properties 裡。'
    $key = ''
    while ($key.Length -lt 20) {
        $key = "$(Read-Host '  API 金鑰')".Trim().Trim('"')
        if ($key.Length -lt 20) { Bad '這看起來不是完整的金鑰，請再貼一次。' }
    }
    $content = "# 你的 Gemini API 金鑰。絕對不要上傳到 GitHub 或分享給別人。`r`napiKey=$key`r`nmodel=$model`r`n"
    [System.IO.File]::WriteAllText($keyFile, $content, $utf8)
    Ok '已儲存 api_key.properties'
}

# ---------------- 3. 測試 ----------------
Step '編譯並測試 AI 連線'
& (Join-Path $root 'run.bat') setup
if ($LASTEXITCODE -eq 0) {
    Write-Host ''
    Write-Host '  設定完成！用 VS Code 打開這個資料夾，等待上課開始。' -ForegroundColor Green
} else {
    Write-Host ''
    Bad '有步驟失敗了（請看上面的訊息），請把這個視窗給助教看。'
}
