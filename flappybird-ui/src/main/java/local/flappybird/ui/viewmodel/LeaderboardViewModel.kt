package local.flappybird.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import local.flappybird.engine.model.ScoreEntry
import local.core.ui.usecase.GetCoinsUseCase
import local.flappybird.engine.usecase.GetScoreHistoryUseCase
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

