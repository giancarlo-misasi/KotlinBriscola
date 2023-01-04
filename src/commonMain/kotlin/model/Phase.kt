package model

enum class Phase {
    SETUP,
    PLAY,
    SCORE_TRICK,
    TAKE,
    SCORE_GAME,
    END;

    companion object {
        fun fromOrdinal(ordinal: Int) = Phase.values()[ordinal]
    }
}