package io.github.karmasmp.karma.player

import java.util.UUID

data class KarmaPlayer(val uuid: UUID, var lives: Int = 3, var state: PlayerState = PlayerState.ALIVE)
