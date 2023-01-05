import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import model.Card
import model.Copyable
import model.Deck
import model.Phase
import model.Player
import model.Trick
import kotlin.random.Random

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
        val pair = deck.serialize()
        result.add(pair.first)
        result.add(pair.second)
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

    override fun toString(): String {
        return "$trick, $players, d: $dealer, p: $player, w: $winner"
    }

    companion object {
        fun deserialize(data: List<Int>): Briscola {
            // need at least all fields + two players
            if (data.size < 9) throw IllegalArgumentException("Not enough data to deserialize.")

            val phase = Phase.fromOrdinal(data[0])
            val deck = Deck.deserialize(Pair(data[1], data[2]))
            val trick = Trick.deserialize(data[3])
            val dealer = data[4]
            val player = data[5]
            val winner = data[6]
            val players = mutableListOf<Player>()
            for (i in 7 until data.size) {
                players.add(Player.deserialize(data[i]))
            }

            return Briscola(deck, trick, players, phase, dealer, player, winner)
        }

        fun simulateOnce(
            state: Briscola,
            seed: Int,
            player1: (state: Briscola) -> Card,
            player2: (state: Briscola) -> Card,
            untilCardsRemaining: Int? = null
        ): Briscola {
            if (state.phase == Phase.SETUP || state.phase == Phase.END) {
                state.setup(seed)
            }

            while (state.phase == Phase.PLAY) {
                if (untilCardsRemaining != null && state.deck.remainingCards() <= untilCardsRemaining) {
                    break
                }

                if (state.player == 0) {
                    state.play(player1(state))
                } else {
                    state.play(player2(state))
                }

                state.scoreAndTake()
            }

            return state
        }

        fun simulateMany(
            iterations: Int,
            player1: (state: Briscola) -> Card,
            player2: (state: Briscola) -> Card
        ): Double {
            var p1wins = 0
            val random = Random(System.currentTimeMillis())
            val winPercentage = runBlocking {
                val wins: List<Int> = (0 until iterations).map{
                    val r = random.nextInt()
                    async(Dispatchers.Default) {
                        val s = Briscola(2)
                        val result = simulateOnce(s, r, player1, player2)
                        val t = result.teamScores()
                        return@async if (t[0] > t[1]) 1 else 0
                    }
                }.awaitAll()
                return@runBlocking wins.sum() / iterations.toDouble()
            }
            return winPercentage
        }

        private fun wrapIncrement(current: Int, max: Int) = (current + 1) % max
    }
}