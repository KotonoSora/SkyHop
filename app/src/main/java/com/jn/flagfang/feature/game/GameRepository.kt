package com.jn.flagfang.feature.game

// import removed as it is in the same package now

interface GameRepository {
    fun saveHighScore(score: Int)
    fun getHighScore(): Int
}
