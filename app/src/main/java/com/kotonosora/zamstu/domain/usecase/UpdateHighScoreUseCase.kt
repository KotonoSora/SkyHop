package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.model.ScoreEntry
import com.kotonosora.zamstu.domain.repository.ScoreRepository

class UpdateHighScoreUseCase(private val scoreRepository: ScoreRepository) {
    suspend operator fun invoke(score: Int, reward: Int) {
        scoreRepository.updateHighScore(score)
        val entry = ScoreEntry(
            score = score,
            reward = reward,
            timestamp = System.currentTimeMillis()
        )
        scoreRepository.saveScoreEntry(entry)
    }
}
