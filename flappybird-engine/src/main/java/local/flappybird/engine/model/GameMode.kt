package local.flappybird.engine.model

object GameMode {
    const val ENDLESS = "endless"
    const val LEVEL = "level"
    const val DAILY = "daily"

    fun of(isDailyChallenge: Boolean, isEndless: Boolean): String =
        if (isDailyChallenge) DAILY else if (isEndless) ENDLESS else LEVEL
}
