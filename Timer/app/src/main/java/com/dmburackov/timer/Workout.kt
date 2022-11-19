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
    }

    var id : Int = 0
    var title : String = ""
    var warmup : Int = 1
    var work : Int = 1
    var rest : Int = 1
    var cycles : Int = 1
    var cooldown : Int = 1
    var color : String = colors[Random.nextInt(0, colors.size - 1)]
}