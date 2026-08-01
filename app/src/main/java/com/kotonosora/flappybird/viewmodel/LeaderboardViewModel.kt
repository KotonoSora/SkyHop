package com.kotonosora.flappybird.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotonosora.flappybird.domain.model.ScoreEntry
import com.kotonosora.flappybird.domain.usecase.GetCoinsUseCase
import com.kotonosora.flappybird.domain.usecase.GetScoreHistoryUseCase
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

