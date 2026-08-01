package com.kotonosora.flappybird.domain.usecase

import com.kotonosora.flappybird.domain.model.ScoreEntry
import com.kotonosora.flappybird.domain.repository.ScoreRepository

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
