package local.flappybird.ui.audio

import androidx.annotation.RawRes
import local.flappybird.ui.R

enum class SfxType(@param:RawRes val rawResId: Int) {
    START(R.raw.start),
    TOUCH(R.raw.touch),
    WIN(R.raw.win),
    LOSE(R.raw.lose),
    MILESTONE(R.raw.milestone),
    COLLECT(R.raw.collect)
}
