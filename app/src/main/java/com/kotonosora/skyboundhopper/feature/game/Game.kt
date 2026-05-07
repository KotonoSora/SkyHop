package com.kotonosora.skyboundhopper.feature.game

data class Bird(var y: Double, var velocity: Double)

class Game {
    var score: Int = 0
        private set

    val bird = Bird(y = 100.0, velocity = 0.0)

    fun tick() {
        bird.velocity += GamePhysics.GRAVITY
        bird.y += bird.velocity
    }

    fun jump() {
        bird.velocity = GamePhysics.JUMP_VELOCITY
    }
}
