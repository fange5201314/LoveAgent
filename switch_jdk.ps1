#!/usr/bin/env pwsh

<#
.SYNOPSIS
    临时切换JDK环境变量的PowerShell脚本

.DESCRIPTION
    这个脚本允许你临时切换JDK版本，而无需修改系统的永久环境变量。
    它会在当前PowerShell会话中设置JAVA_HOME和PATH环境变量。

.PARAMETER Version
    指定要切换的JDK版本，例如：21, 17, 11等。

.PARAMETER List
    列出系统中检测到的JDK版本。

.PARAMETER Reset
    重置JDK环境变量为原始状态。

.EXAMPLE
    .\switch_jdk.ps1 -Version 21
    切换到JDK 21

.EXAMPLE
    .\switch_jdk.ps1 -List
    列出系统中检测到的JDK版本

.EXAMPLE
    .\switch_jdk.ps1 -Reset
    重置JDK环境变量
#>

param (
    [string]$Version,
    [switch]$List,
    [switch]$Reset
)

# 保存原始环境变量（只在第一次运行时保存）
if (-not (Test-Path -Path "env:ORIGINAL_JAVA_HOME")) {
    if (Test-Path -Path "env:JAVA_HOME") {
        Set-Item -Path "env:ORIGINAL_JAVA_HOME" -Value (Get-Item -Path "env:JAVA_HOME").Value
    } else {
        Set-Item -Path "env:ORIGINAL_JAVA_HOME" -Value ""
    }
}

if (-not (Test-Path -Path "env:ORIGINAL_PATH")) {
    Set-Item -Path "env:ORIGINAL_PATH" -Value (Get-Item -Path "env:PATH").Value
}

# 重置环境变量
if ($Reset) {
    Write-Host "正在重置JDK环境变量..."
    if ((Get-Item -Path "env:ORIGINAL_JAVA_HOME").Value -ne "") {
        Set-Item -Path "env:JAVA_HOME" -Value (Get-Item -Path "env:ORIGINAL_JAVA_HOME").Value
    } else {
        Remove-Item -Path "env:JAVA_HOME" -ErrorAction SilentlyContinue
    }
    Set-Item -Path "env:PATH" -Value (Get-Item -Path "env:ORIGINAL_PATH").Value
    Write-Host "JDK环境变量已重置为原始状态。"
    return
}

# 列出已安装的JDK版本
if ($List) {
    Write-Host "正在搜索系统中的JDK安装..."
    
    # 搜索常见的JDK安装路径
    $searchPaths = @(
        "C:\Program Files\Java",
        "C:\Program Files (x86)\Java",
        "$HOME\Java",
        "D:\Java"
    )
    
    $jdkVersions = @()
    
    foreach ($path in $searchPaths) {
        if (Test-Path -Path $path) {
            $directories = Get-ChildItem -Path $path -Directory | Where-Object { $_.Name -match '^jdk\d+' }
            foreach ($dir in $directories) {
                $version = $dir.Name -replace '^jdk(\d+).*', '$1'
                $jdkVersions += [PSCustomObject]@{ Version = $version; Path = $dir.FullName }
            }
        }
    }
    
    if ($jdkVersions.Count -eq 0) {
        Write-Host "未找到已安装的JDK。"
    } else {
        Write-Host "找到以下JDK版本："
        $jdkVersions | Format-Table -AutoSize
    }
    return
}

# 切换JDK版本
if (-not [string]::IsNullOrEmpty($Version)) {
    Write-Host "正在切换到JDK $Version..."
    
    # 搜索常见的JDK安装路径
    $searchPaths = @(
        "C:\Program Files\Java",
        "C:\Program Files (x86)\Java",
        "$HOME\Java",
        "D:\Java"
    )
    
    $jdkPath = $null
    
    foreach ($path in $searchPaths) {
        if (Test-Path -Path $path) {
            $directories = Get-ChildItem -Path $path -Directory | Where-Object { $_.Name -match "^jdk$Version" -or $_.Name -match "^jdk$Version\." }
            if ($directories.Count -gt 0) {
                $jdkPath = $directories[0].FullName
                break
            }
        }
    }
    
    if (-not $jdkPath) {
        Write-Host "错误: 未找到JDK $Version。请确保已安装JDK $Version并尝试使用完整路径。"
        return
    }
    
    # 设置JAVA_HOME
    Set-Item -Path "env:JAVA_HOME" -Value $jdkPath
    
    # 更新PATH
    $newPath = "$jdkPath\bin;" + (Get-Item -Path "env:ORIGINAL_PATH").Value
    # 移除可能存在的其他Java路径
    $newPath = ($newPath -split ';' | Where-Object { $_ -notmatch 'java|jdk|jre' }) -join ';'
    # 添加新的Java路径
    $newPath = "$jdkPath\bin;$newPath"
    Set-Item -Path "env:PATH" -Value $newPath
    
    Write-Host "成功切换到JDK $Version。"
    Write-Host "JAVA_HOME: $jdkPath"
    Write-Host "可以通过运行 'java -version' 验证。"
    return
}

# 如果没有提供参数，显示帮助
Write-Host "用法: .\switch_jdk.ps1 [-Version <version>] [-List] [-Reset]"
Write-Host "例如: .\switch_jdk.ps1 -Version 21"
Write-Host "      .\switch_jdk.ps1 -List"
Write-Host "      .\switch_jdk.ps1 -Reset"