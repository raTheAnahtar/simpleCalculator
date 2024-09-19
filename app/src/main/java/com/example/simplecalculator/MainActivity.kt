package com.example.simplecalculator

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity(){
    private var valueString = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    companion object {
        private val allOperationList: List<Char> = listOf('/', '*', '-', '+')
        private val plusAndMinus = listOf("+", "-")
    }

    fun onClickClear(view: View) {
        setDefaultValue()
        updateDisplay()
        findViewById<TextView>(R.id.result).text = ""
    }

    fun onClickNumber(view: View) {
        when (view.id) {
            R.id.num1 -> valueString += "1"
            R.id.num2 -> valueString += "2"
            R.id.num3 -> valueString += "3"
            R.id.num4 -> valueString += "4"
            R.id.num5 -> valueString += "5"
            R.id.num6 -> valueString += "6"
            R.id.num7 -> valueString += "7"
            R.id.num8 -> valueString += "8"
            R.id.num9 -> valueString += "9"
            R.id.num0 -> valueString += "0"
        }
        if(valueString.length == 2 && valueString[0] == '0') valueString = valueString.drop(1)
        updateDisplay()
    }

    fun onClickOperation(view: View) {
        if(allOperationList.contains(valueString.last())) return
        when (view.id) {
            R.id.divide -> valueString += "/"
            R.id.multiply -> valueString += "*"
            R.id.subtract -> valueString += "-"
            R.id.add -> valueString += "+"
        }
        updateDisplay()
    }

    fun onClickDecimal(view: View) {
        val lastChar = valueString.last()
        if(allOperationList.contains(lastChar)) return
        if(!valueString.isDecimalPossible()) return
        valueString += "."
        updateDisplay()
    }
    fun onClickErase(view: View) {
        valueString = valueString.dropLast(1)
        if(valueString.isEmpty()) valueString += "0"
        updateDisplay()
    }

    fun onClickCalculate(view: View) {
        // Check for invalid operation
        if (valueString.split('/', '*', '-', '+').contains("")) {
            findViewById<TextView>(R.id.result).text = "INVALID"
            return
        }

        val listOfAddAndSub = valueString.splitByAdditionAndSubtraction()
        var sum = 0.0
        // calculate the multiplication and divisions
        for(item in listOfAddAndSub) {
            val sign = item.first
            var numberString = item.second
            if (numberString.contains("/") || numberString.contains("*")) {
                // handle the division and multiplication
                val listNum = numberString.split("/", "*")
                val listOp = Regex("[/*]").findAll(numberString).map { it.value }.toList()
                var first = listNum[0].toDouble()
                var second = listNum[1].toDouble()
                var result = 0.0
                for (i in listOp.indices) {
                    when (listOp[i]) {
                        "*" -> result = first * second
                        "/" -> result = first / second
                    }
                    if (i < listOp.size-1) {
                        first = result
                        second = listNum[i+2].toDouble()
                    }
                }
                numberString = result.toString()
            }
            // add and subtract
            when(sign) {
                // handle the addition and subtraction
                '+' -> sum += numberString.toDouble()
                '-' -> sum += -1 * numberString.toDouble()
            }
        }
        findViewById<TextView>(R.id.result).text = sum.toString()
        setDefaultValue()
    }


    private fun updateDisplay() {
        findViewById<TextView>(R.id.display).text = valueString
    }

    private fun setDefaultValue() {
        valueString = "0"
    }

    private fun String.isDecimalPossible(): Boolean {
        val pair = this.findLastAnyOf(allOperationList.map{it.toString()})
        var numString = ""
        if(pair == null) numString = valueString
        else numString = this.substring(pair.first)

        if(numString.contains(".")) return false
        return true
    }

    private fun String.splitByAdditionAndSubtraction(): List<Pair<Char, String>> {
        var operationString = if(this[0].isDigit()) "+$this" else this
        var pairList: MutableList<Pair<Char, String>> = mutableListOf()

        var nextOp = operationString.findAnyOf(plusAndMinus, 1)
        while(nextOp != null) {
            val subString = operationString.substring(0, nextOp.first)
            pairList.add(Pair(subString[0], subString.drop(1)))
            operationString = operationString.drop(nextOp.first)
            nextOp = operationString.findAnyOf(plusAndMinus, 1)
        }
        pairList.add(Pair(operationString[0], operationString.drop(1)))

        return pairList
    }
}