package com.jn.flagfang.feature.game

data class Animal(var y: Double, var velocity: Double)

class Game {
    var score: Int = 0
        private set

    val Animal = Animal(y = 100.0, velocity = 0.0)

    fun tick() {
        Animal.velocity += GamePhysics.GRAVITY
        Animal.y += Animal.velocity
    }

    fun jump() {
        Animal.velocity = GamePhysics.JUMP_VELOCITY
    }
}
