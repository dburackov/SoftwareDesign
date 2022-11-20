package com.dmburackov.timer

import kotlin.random.Random

class Workout {
    companion object {
        val colors = listOf(
            "C0C0C0",//1
            "808080",//2
            "FF0000",//3
            "800000",//4
            "FF00FF",//5
            "BB86FC",//6
            "7F0DEC",//7
            "808000",//8
            "6200EE",//9
            "0000FF",//10
            "008080",//11
            "008000",//12
        )
        fun getStageName(pos : Int, size : Int) : String {
            return when (pos) {
                0 -> {
                    return "Warm up"
                }
                size - 1 -> {
                    return "Cooldown"
                }
                else -> {
                    return if (pos % 2 == 1) "Work" else "Rest"
                }
            }
        }
    }

    var id : Int = 0
    var title : String = ""
    var warmup : Int = 1
    var work : Int = 1
    var rest : Int = 1
    var cycles : Int = 1
    var cooldown : Int = 1
    var color : String = colors[Random.nextInt(0, colors.size - 1)]

    fun getValues() : IntArray {
        val result = IntArray((cycles + 1) * 2)
        result[0] = warmup
        result[result.size - 1] = cooldown
        for (i in 0 until cycles) {
            result[i * 2 + 1] = work
            result[i * 2 + 2] = rest
        }
        return result
    }
//
//    fun getStages() : Array<String> {
//        val result = Array((cycles + 1) * 2) {""}
//        result[0] = "Warm up"
//        result[result.size - 1] = "Cooldown"
//        for (i in 0 until cycles) {
//            result[i * 2 + 1] = "Work"
//            result[i * 2 + 2] = "Rest"
//        }
//        return result
//    }
}