package local.core.ui.usecase

import local.core.ui.data.AudioSettings
import kotlinx.coroutines.flow.Flow

data class AudioSettingsState(
    val musicEnabled: Boolean,
    val sfxEnabled: Boolean
)

class GetAudioSettingsUseCase(private val audio: AudioSettings) {
    val musicEnabledFlow: Flow<Boolean> = audio.musicEnabledFlow
    val sfxEnabledFlow: Flow<Boolean> = audio.sfxEnabledFlow
}
