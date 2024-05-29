package com.ccs.fish.scan.utils

sealed class Navigation(val route: String) {
    data object Main : Navigation("main")

    data object Scan : Navigation("Scan")
    data object Instruction : Navigation("Instruction")
    data object Gallery : Navigation("Gallery")
    data object History : Navigation("History")

    data object SingleCapture: Navigation("SingleCapture")
}