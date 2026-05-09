package com.jn.flagfang.domain

import com.jn.flagfang.feature.game.Game
import org.junit.Assert.assertEquals

import org.junit.Test

class GameTest {

    @Test
    fun `new game starts with score of zero`() {
        val game = Game()
        assertEquals(0, game.score)
    }

    @Test
    fun `Animal position is centered when game starts`() {
        val game = Game()
        assertEquals(100.0, game.Animal.y, 0.1)
    }

    @Test
    fun `Animal falls due to gravity`() {
        val game = Game()
        val initialY = game.Animal.y
        game.tick()
        assert(game.Animal.y > initialY)
    }

    @Test
    fun `Animal jumps`() {
        val game = Game()
        game.jump()
        assert(game.Animal.velocity < 0.0)
    }

}
