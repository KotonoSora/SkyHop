package com.jn.flagfang.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val selectedSkinFlow: Flow<String>
    val coinsFlow: Flow<Int>
    val purchasedItemsFlow: Flow<Set<String>>
    val shieldCountFlow: Flow<Int>
    val multiplierCountFlow: Flow<Int>
    val autoPlayCountFlow: Flow<Int>
    val musicEnabledFlow: Flow<Boolean>
    val sfxEnabledFlow: Flow<Boolean>
    val lastDailyRewardTimeFlow: Flow<Long>

    suspend fun updateSelectedSkin(skinId: String)
    suspend fun addCoins(amount: Int)
    suspend fun spendCoins(amount: Int): Boolean
    suspend fun purchaseItem(itemId: String)
    suspend fun addPowerUp(typeId: String)
    suspend fun usePowerUp(typeId: String): Boolean
    suspend fun toggleMusic(enabled: Boolean)
    suspend fun toggleSfx(enabled: Boolean)
    suspend fun claimDailyReward(): Boolean
}
