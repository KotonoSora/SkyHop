package local.flappybird.engine.game

/**
 * Coin-reward rules for runs. Lives in the engine (domain layer) so every
 * host app and game mode shares one policy instead of re-implementing it in
 * ViewModels.
 */
object RewardPolicy {
    private const val DAILY_CHALLENGE_WIN_REWARD = 50
    private const val WIN_REWARD_PER_LEVEL = 5

    fun winReward(level: Int, isDailyChallenge: Boolean): Int =
        if (isDailyChallenge) DAILY_CHALLENGE_WIN_REWARD else level * WIN_REWARD_PER_LEVEL

    fun gameOverReward(score: Int): Int = score / 2
}
