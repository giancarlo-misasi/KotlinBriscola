import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.events.input.mouse.MouseEvent
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.App
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.View
import dev.misasi.giancarlo.ux.views.HorizontalLayout
import dev.misasi.giancarlo.ux.views.VerticalLayout
import model.Card
import model.Face
import model.Player
import model.Suit

var cardSet = 0
val cardSets = listOf("Napoletane", "Piacentine", "Romagnole", "Sarde", "Siciliane")
fun cardMaterial(card: Card) = "${cardSets[cardSet]}CardSet${card.materialName}"

fun nextCardSet() {
    cardSet = (cardSet + 1) % cardSets.size
}

class BriscolaController {
    fun playCard(card: Card) {
        println(card)
    }
}

class PlayerView(
    private val controller: BriscolaController
) : View() {
    var player: Player? = null
    var selectedCard: Card? = null

    init {
        size = Vector2i(320, 256)
    }

    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        val model = player ?: return
        var offset = 0
        for (card in model.cards())  {
            state.putSprite(cardMaterial(card), AffineTransform(
                scale = Vector2f(160, 256),
                translation = Vector2f(offset, 0)
            ))
//            break
            offset += 80
        }

//        offset = 0
//        for (card in model.cards())  {
//            if (card == selectedCard) {
//                state.putSprite(
//                    cardMaterial(card), AffineTransform(
//                        scale = Vector2f(160, 256),
//                        translation = Vector2f(offset, 0)
//                    )
//                )
//            }
//            offset += 80
//        }
    }

    override fun onEvent(context: AppContext, event: Event): Boolean {
        if (event is MouseEvent) {
//            println(event)
        }

        if (event is MouseButtonEvent) {
            selectedCard?.let { controller.playCard(it) }
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
        Vector2i(800, 800),
        fullScreen = false,
        refreshRate = 60,
        vsync = false,
        Vector2i(800, 800),
    )
    app.enableMouseEvents(true)
    app.enableMouseButtonEvents(true)

    val rootView = VerticalLayout()
    val child1 = HorizontalLayout()
    val child2 = HorizontalLayout()
    val child3 = HorizontalLayout()
    rootView.add(child1)
    rootView.add(child2)
    rootView.add(child3)

    val controller = BriscolaController()
    val p1 = PlayerView(controller)
    child1.add(p1)

    // TODO: Deck view

    val p2 = PlayerView(controller)
    child3.add(p2)

    p1.player = Player()
    p1.player?.add(Card(Suit.SPADE, Face.TWO))
    p1.player?.add(Card(Suit.DIAMOND, Face.KING))
    p1.player?.add(Card(Suit.CLUB, Face.SEVEN))

    p2.player = Player()
    p2.player?.add(Card(Suit.SPADE, Face.FIVE))
    p2.player?.add(Card(Suit.HEART, Face.PRINCE))
    p2.player?.add(Card(Suit.DIAMOND, Face.FOUR))

    app.run(rootView);
}