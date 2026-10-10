package local.flappybird.engine.usecase

import local.flappybird.engine.model.ScoreEntry
import local.flappybird.engine.repo.ScoreRepository

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
