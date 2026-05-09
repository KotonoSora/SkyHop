package com.jn.flagfang.feature.game

// import removed as it is in the same package now

class GameRepositoryImpl : GameRepository {
    private var highScore: Int = 0

    override fun saveHighScore(score: Int) {
        if (score > highScore) {
            highScore = score
        }
    }

    override fun getHighScore(): Int = highScore
}
