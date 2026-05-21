package com.jn.flagfang.feature.game

// imports removed, all in same package

class GameUseCases(private val gameRepository: GameRepository) {
    fun startNewGame(): Game {
        return Game()
    }
}
