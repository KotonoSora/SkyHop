package local.flappybird.engine.model

enum class RunEndCause {
    WIN,
    COLLISION
}

data class RunRecord(
    val mode: String,
    val level: Int,
    val score: Int,
    val reward: Int,
    val durationMs: Long,
    val pipesPassed: Int,
    val endCause: RunEndCause,
    val shieldUses: Int = 0,
    val multiplierUses: Int = 0,
    val autoPlayUses: Int = 0,
    val deathX: Float? = null,
    val deathY: Float? = null,
    val startedAt: Long,
    val endedAt: Long
) {
    /** Display projection for the leaderboard, which shows score/reward/time. */
    fun toScoreEntry(): ScoreEntry =
        ScoreEntry(score = score, reward = reward, timestamp = endedAt)
}
