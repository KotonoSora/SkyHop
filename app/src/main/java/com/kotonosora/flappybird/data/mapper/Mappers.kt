package com.kotonosora.flappybird.data.mapper

import com.kotonosora.flappybird.domain.model.ScoreEntry

/**
 * Extension functions to map between Data and Domain layers.
 */

fun String.toScoreEntry(): ScoreEntry? {
    val parts = this.split(":")
    if (parts.size < 3) return null
    return ScoreEntry(
        score = parts[0].toIntOrNull() ?: 0,
        reward = parts[1].toIntOrNull() ?: 0,
        timestamp = parts[2].toLongOrNull() ?: 0L
    )
}

fun ScoreEntry.toRawString(): String {
    return "$score:$reward:$timestamp"
}
