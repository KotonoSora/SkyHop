package com.kotonosora.skyboundhopper.application

import com.kotonosora.skyboundhopper.domain.model.Game
import com.kotonosora.skyboundhopper.domain.repository.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class GameUseCasesTest {

    @Test
    fun `start new game resets score`() {
        val mockRepository = object : GameRepository {
            override fun saveHighScore(score: Int) {}
            override fun getHighScore(): Int = 0
        }
        val useCases = GameUseCases(mockRepository)
        val game = useCases.startNewGame()
        assertEquals(0, game.score)
    }
}
