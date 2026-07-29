package com.jn.flagfang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.flagfang.domain.model.ScoreEntry
import com.jn.flagfang.domain.usecase.GetCoinsUseCase
import com.jn.flagfang.domain.usecase.GetScoreHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel(
    private val getScoreHistoryUseCase: GetScoreHistoryUseCase,
    private val getCoinsUseCase: GetCoinsUseCase
) : ViewModel() {

    val scoreHistory: StateFlow<List<ScoreEntry>> = getScoreHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val coins: StateFlow<Int> = getCoinsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )
}

