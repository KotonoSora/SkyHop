package com.kotonosora.skyboundhopper.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kotonosora.skyboundhopper.feature.game.GameRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameRepositoryImplTest {

    @Test
    fun saveAndGetHighScore() {
        val repository = GameRepositoryImpl()
        repository.saveHighScore(100)
        assertEquals(100, repository.getHighScore())
    }
}
