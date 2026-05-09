package com.jn.flagfang.domain.repository

import kotlinx.coroutines.flow.Flow

interface AdRewardRepository {
    val canWatchAdFlow: Flow<Boolean>
    suspend fun recordAdWatched()
}
