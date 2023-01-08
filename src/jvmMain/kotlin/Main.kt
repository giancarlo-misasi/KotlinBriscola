import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.drawing.Font
import dev.misasi.giancarlo.drawing.Rgb8
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.mouse.MouseButton
import dev.misasi.giancarlo.events.input.mouse.MouseButtonAction
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.events.input.mouse.MouseEvent
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Rotation
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.App
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.View
import dev.misasi.giancarlo.ux.views.Button
import dev.misasi.giancarlo.ux.views.HorizontalLayout
import dev.misasi.giancarlo.ux.views.VerticalLayout
import model.Card
import model.Deck
import model.Player
import model.Trick
import kotlin.system.exitProcess

var cardSet = 0
val cardSets = listOf("Napoletane", "Piacentine", "Romagnole", "Sarde", "Siciliane")
fun cardMaterial(materialName: String) = "${cardSets[cardSet]}CardSet${materialName}"
fun cardMaterial(card: Card) = cardMaterial(card.materialName)

fun nextCardSetPlease() {
    cardSet = (cardSet + 1) % cardSets.size
}

class BriscolaController {

    val rootView = VerticalLayout()
    val p1 = PlayerView(this)
    val p2 = PlayerView(this)
    val dv = DeckView(this)
    val tv = TrickView(this)
    var state = Briscola(2)

    init {
        val buttons = HorizontalLayout()
        buttons.add(Button("SardeCardSetBlank", Font.ROBOTO24.copy(color = Rgb8.BLACK),"New Game", Vector2i(200, 100)) {
            newGame()
        })
        buttons.add(Button("SardeCardSetBlank", Font.ROBOTO24.copy(color = Rgb8.BLACK),"Change Style", Vector2i(200, 100)) {
            nextCardSetPlease()
        })
        buttons.add(Button("SardeCardSetBlank", Font.ROBOTO24.copy(color = Rgb8.BLACK),"Quit", Vector2i(200, 100)) {
            exitProcess(0)
        })
        rootView.add(buttons)

        val child1 = HorizontalLayout()
//        child1.padding = Inset(4, 4, 4, 4)
        child1.add(p1)
        val child2 = HorizontalLayout()
//        child2.padding = Inset(4, 4, 4, 4)
        child2.add(dv)
        val child3 = HorizontalLayout()
//        child3.padding = Inset(4, 4, 4, 4)
        child3.add(tv)
        val child4 = HorizontalLayout()
//        child4.padding = Inset(4, 4, 4, 4)
        child4.add(p2)
        rootView.add(child1)
        rootView.add(child2)
        rootView.add(child3)
        rootView.add(child4)

        newGame()
    }

    fun newGame() {
        state = Briscola(2)
        state.setup(System.currentTimeMillis().toInt())
        updateViews()
    }

    fun playCard(card: Card) {
        state.play(card)
        println(state)
    }

    fun takeCard() {
        state.scoreAndTake()
    }

    fun nextCardSet() {
        nextCardSetPlease()
    }

    fun updateViews() {
        p1.game = state
        p1.player = state.players[0]
        p1.selectedCard = null

        p2.game = state
        p2.player = state.players[1]
        p2.selectedCard = null

        dv.deck = state.deck
        tv.trick = state.trick
    }
}

class TrickView(
    private val controller: BriscolaController
) : View() {
    var trick: Trick? = null

    init {
        size = Vector2i(320, 256)
    }

    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        val model = trick ?: return
        if (model.cards().isEmpty()) return

        var offset = 0
        val increment = 256 / model.cards().size
        for (card in model.cards()) {
            state.putSprite(
                cardMaterial(card), AffineTransform(
                    scale = Vector2f(160, 256),
                    translation = Vector2f(offset, 0)
                )
            )
            offset += increment
        }
    }
}

class DeckView(
    private val controller: BriscolaController
) : View() {
    var deck: Deck? = null

    init {
        size = Vector2i(320, 256)
    }

    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        val model = deck ?: return
        state.putSprite(
            cardMaterial(model.bottom()), AffineTransform(
                scale = Vector2f(160, 256),
                translation = Vector2f(120, 0),
                rotation = Rotation.DEGREES_90,
            )
        )
        val materialName = if (model.isEmpty()) "Blank" else "Back1"
        state.putSprite(
            cardMaterial(materialName), AffineTransform(
                scale = Vector2f(160, 256),
                translation = Vector2f(0, 0)
            )
        )
        state.putText("Really? AW WA VA TA", font = Font.ROBOTO16.copy(color = Rgb8.RED))
    }

    override fun onEvent(context: AppContext, event: Event): Boolean {
        val model = deck ?: return false

        if (event is MouseButtonEvent
            && event.button == MouseButton.LEFT
            && event.action == MouseButtonAction.RELEASE
        ) {
            controller.takeCard()
            return true
        }

        return super.onEvent(context, event)
    }
}

class PlayerView(
    private val controller: BriscolaController
) : View() {
    var game: Briscola? = null
    var player: Player? = null
    var selectedCard: Card? = null

    init {
        size = Vector2i(736, 256)
    }

    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        val model = player ?: return
        if (model.cards().isEmpty()) return

        var offset = 0
        val increment = 256 / model.cards().size
        for (card in model.cards()) {
            if (card != selectedCard) {
                state.putSprite(
                    cardMaterial(card), AffineTransform(
                        scale = Vector2f(160, 256),
                        translation = Vector2f(offset, 0)
                    )
                )
            }
            offset += increment
        }

        offset = 0
        for (card in model.cards()) {
            if (card == selectedCard) {
                state.putSprite(
                    cardMaterial(card), AffineTransform(
                        scale = Vector2f(160, 256),
                        translation = Vector2f(offset, 0)
                    )
                )
            }
            offset += increment
        }

        if (game?.currentPlayer() == player) {
            state.putSprite(
                cardMaterial("Blank"), AffineTransform(
                    scale = Vector2f(160, 256),
                    translation = Vector2f(offset + increment, 0)
                )
            )
        }
    }

    override fun onEvent(context: AppContext, event: Event): Boolean {
        val model = player ?: return false
        val contains = absoluteBounds?.contains(event.absolutePosition) ?: false
        if (!contains) return false

        val position = event.absolutePosition.minus(absolutePosition!!)
        println(position)

        if (event is MouseEvent) {
            val cardCount = model.cards().size
            if (cardCount > 0) {
                val increment = 256 / cardCount
                for (i in 0 until cardCount) {
                    if (position.x < increment * (i + 1)) {
                        selectedCard = model.cards()[i]
                        return true
                    }
                }
            }
        }

        if (event is MouseButtonEvent
            && event.button == MouseButton.RIGHT
            && event.action == MouseButtonAction.RELEASE
        ) {
            controller.newGame()
        }

        if (event is MouseButtonEvent
            && event.button == MouseButton.MIDDLE
            && event.action == MouseButtonAction.RELEASE
        ) {
            controller.nextCardSet()
        }


        if (event is MouseButtonEvent
            && event.button == MouseButton.LEFT
            && event.action == MouseButtonAction.RELEASE
        ) {
            selectedCard?.let { controller.playCard(it) }
            return true
        }

        return super.onEvent(context, event)
    }
}

//class BriscolaView : ViewGroup() {
//
//    var state: Briscola? = null
//
//    init {
//        val table = VerticalLayout()
//        val top = HorizontalLayout()
//        val mid = HorizontalLayout()
//        val bot = HorizontalLayout()
//        table.add(top)
//        table.add(mid)
//        table.add(bot)
//        add(table)
//    }
//}

//fun human(state: Briscola): Card {
//    val cards = state.currentPlayer().cards();
//    println(state)
//    val rec = BriscolaMcts.calculateMctsMove(state)
//    println("rec=${rec}")
//    val i = readln().toInt()
//    return cards[i]
//}
//
//fun random(state: Briscola): Card {
//    val cards = state.currentPlayer().cards();
//    val i = Random(System.currentTimeMillis()).nextInt(cards.size)
//    return cards[i]
//}

fun main() {
//    val state = Briscola.simulateOnce(Briscola(2), 1, ::random, ::random, 2)
//    Briscola.simulateOnce(state, 0, ::human, ::human)
//    println(state)

    val app = App(
        "title",
        Vector2i(1600, 1400),
        fullScreen = false,
        refreshRate = 60,
        vsync = false,
        Vector2i(1600, 1400),
    )
    app.enableMouseEvents(true)
    app.enableMouseButtonEvents(true)
    app.debugLayout = true

    val controller = BriscolaController()

    app.run(controller.rootView);
}