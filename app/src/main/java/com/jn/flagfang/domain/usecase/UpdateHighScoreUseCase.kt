package com.jn.flagfang.domain.usecase

import com.jn.flagfang.domain.model.ScoreEntry
import com.jn.flagfang.domain.repository.ScoreRepository

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
