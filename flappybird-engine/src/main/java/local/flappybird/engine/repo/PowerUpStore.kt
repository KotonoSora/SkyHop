package local.flappybird.engine.repo

/**
 * Narrow power-up wallet. The app's settings repository adapts to this so
 * `:engine` never depends on Android DataStore or app classes.
 */
interface PowerUpStore {
    suspend fun usePowerUp(typeId: String): Boolean
}
