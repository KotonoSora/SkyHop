package com.kotonosora.skyboundhopper.domain

import com.kotonosora.skyboundhopper.domain.model.Game
import org.junit.Assert.assertEquals

import org.junit.Test

class GameTest {

    @Test
    fun `new game starts with score of zero`() {
        val game = Game()
        assertEquals(0, game.score)
    }

    @Test
    fun `bird position is centered when game starts`() {
        val game = Game()
        assertEquals(100.0, game.bird.y, 0.1)
    }

    @Test
    fun `bird falls due to gravity`() {
        val game = Game()
        val initialY = game.bird.y
        game.tick()
        assert(game.bird.y > initialY)
    }

    @Test
    fun `bird jumps`() {
        val game = Game()
        game.jump()
        assert(game.bird.velocity < 0.0)
    }

}
