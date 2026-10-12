package local.flappybird.engine.usecase

import local.flappybird.engine.model.PowerUpType
import local.flappybird.engine.repo.PowerUpStore

class UsePowerUpUseCase(private val store: PowerUpStore) {
    suspend operator fun invoke(typeId: String): Boolean {
        PowerUpType.fromId(typeId) ?: return false
        return store.usePowerUp(typeId)
    }
}
