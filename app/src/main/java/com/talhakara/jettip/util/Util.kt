package com.talhakara.jettip.util


fun calculateTotalTip(totalBill: Double, tipPercent: Int): Double {
    if (totalBill > 1 && totalBill.toString().isNotEmpty()) {
        return (totalBill * tipPercent) / 100
    } else {
        return 0.0
    }
}
fun calculateTotalPerPerson(totalBill: Double,
                            splitBy: Int,
                            tipPercent: Int): Double {

    val bill = calculateTotalTip(totalBill = totalBill, tipPercent = tipPercent) + totalBill
    return (bill/splitBy)
}