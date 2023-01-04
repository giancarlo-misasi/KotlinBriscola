import model.Card
import model.Copyable
import model.Deck
import model.Phase
import model.Player
import model.Trick

data class Briscola(
    val deck: Deck,
    val trick: Trick,
    val players: List<Player>,
    var phase: Phase,
    var dealer: Int,
    var player: Int,
    var winner: Int
) : Copyable<Briscola> {
    constructor(players: Int) : this(Deck(), Trick(), List(players) { Player() }, Phase.SETUP, 0, 0, 0)

    fun currentPlayer(): Player = players[player]

    fun playerScores(): List<Int> = players.map { it.points }.toList()

    fun teamScores(): List<Int> {
        val result = mutableListOf(0, 0)
        for (i in players.indices) result[i % 2] += players[i].points
        return result.toList()
    }

    fun setup(seed: Int) {
        deck.shuffle(seed)
        trick.trump = deck.bottom().suit
        players.forEach { it.reset() }

        dealer = wrapIncrement(dealer, players.size)
        player = wrapIncrement(dealer, players.size)
        winner = player

        val remaining = deck.size() - 3 * players.size
        while (deck.remainingCards() > remaining) {
            players[player].add(deck.deal())
            player = wrapIncrement(player, players.size)
        }

        phase = Phase.PLAY
    }

    fun play(card: Card): Boolean {
        if (!players[player].remove(card)) return false

        if (trick.add(card)) {
            winner = player
        }

        player = wrapIncrement(player, players.size)

        if (trick.full(players.size)) {
            phase = Phase.SCORE_TRICK
        }
        return true
    }

    fun scoreTrick() {
        players[winner].add(trick.value())
        player = winner
        trick.clear()
        phase = Phase.TAKE

        if (players.all { it.isEmpty() }) {
            phase = Phase.SCORE_GAME
        }
    }

    fun take() {
        if (!deck.isEmpty()) {
            players[player].add(deck.deal())
            player = wrapIncrement(player, players.size)
        }

        if (deck.isEmpty() || players.all { it.remainingCards() == 3 }) {
            phase = Phase.PLAY
        }
    }

    fun scoreGame() {
        phase = Phase.END
    }

    // For AI algorithms
    fun scoreAndTake() {
        if (phase == Phase.SCORE_TRICK) scoreTrick()
        while (phase == Phase.TAKE) take()
        if (phase == Phase.SCORE_GAME) scoreGame()
    }

    fun serialize(): List<Int> {
        val result = mutableListOf<Int>()
        result.add(phase.ordinal)
        result.add(deck.serialize())
        result.add(trick.serialize())
        result.add(dealer)
        result.add(player)
        result.add(winner)
        result.addAll(players.map { it.serialize() })
        return result.toList()
    }

    override fun copy(): Briscola {
        return deserialize(serialize())
    }

    companion object {
        fun deserialize(data: List<Int>): Briscola {
            // need at least all fields + two players
            if (data.size < 8) throw IllegalArgumentException("Not enough data to deserialize.")

            val phase = Phase.fromOrdinal(data[0])
            val deck = Deck.deserialize(data[1])
            val trick = Trick.deserialize(data[2])
            val dealer = data[3]
            val player = data[4]
            val winner = data[5]
            val players = mutableListOf<Player>()
            for (i in 6 until data.size) {
                players.add(Player.deserialize(data[i]))
            }

            return Briscola(deck, trick, players, phase, dealer, player, winner)
        }

        private fun wrapIncrement(current: Int, max: Int) = (current + 1) % max
    }
}