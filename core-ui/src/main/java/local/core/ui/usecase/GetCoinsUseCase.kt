package local.core.ui.usecase

import local.core.ui.data.CoinAccount
import kotlinx.coroutines.flow.Flow

class GetCoinsUseCase(private val coins: CoinAccount) {
    operator fun invoke(): Flow<Int> = coins.coinsFlow
}
