package com.ccs.fish.scan.utils

fun getFileNameFromFilePath(filePath: String): String {
    return filePath.substring(filePath.lastIndexOf("/") + 1)
}