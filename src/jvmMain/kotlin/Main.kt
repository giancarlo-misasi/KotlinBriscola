import dev.misasi.giancarlo.MyViewRenderer
import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.mouse.MouseButton
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.App
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.Renderer
import dev.misasi.giancarlo.ux.View
import dev.misasi.giancarlo.ux.transitions.Slide
import dev.misasi.giancarlo.ux.views.HorizontalLayout
import dev.misasi.giancarlo.ux.views.VerticalLayout
import dev.misasi.giancarlo.windowHeight
import dev.misasi.giancarlo.windowWidth
import model.Card

class MyView : View() {
    override fun onEvent(context: AppContext, event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (event.button == MouseButton.LEFT) {
                context.go(MyView(), inTransition = Slide.slideUp(
                    context.viewport.designedResolution.y.toFloat(),
                    Vector2f(0, context.viewport.designedResolution.y)
                )
                )
            }
        }
        return false
    }
}

class MyViewRenderer : Renderer {
    override fun render(target: Any, context: AppContext, state: DrawState) {
        if (target !is MyView) return

        val contentSize = target.onMeasure(context)
        state.putSprite("White", AffineTransform(
            scale = Vector2f(400, 400),
            translation = Vector2f(400f, 400f),
        )
        )
        state.putSprite("Black", AffineTransform(
            scale = Vector2f(400, 400),
        )
        )
        state.putSprite("PlayerWalkDown1", AffineTransform(
            scale = Vector2f(100, 100),
            translation = Vector2f(100, 100),
        )
        )
    }
}

fun human(state: Briscola): Card {
    val cards = state.currentPlayer().cards();
    println(state)
    val rec = BriscolaMcts.calculateMctsMove(state)
    println("rec=${rec}")
    val i = readln().toInt()
    return cards[i]
}

fun main() {
    val state = Briscola.simulateOnce(1, ::human, ::human)
    println(state)

//    val app = App(
//        "title",
//        Vector2i(windowWidth + 100, windowHeight),
//        fullScreen = false,
//        refreshRate = 60,
//        vsync = false,
//        Vector2i(windowWidth, windowHeight),
//    )
//    app.enableResizeEvents(true)
//    app.enableMouseEvents(true)
//    app.enableMouseButtonEvents(true)
//    app.register(dev.misasi.giancarlo.MyView::class, MyViewRenderer())
//
//    val rootView = HorizontalLayout()
//    val child1 = VerticalLayout()
//    val child2 = dev.misasi.giancarlo.MyView()
//    child1.add(child2)
//    rootView.children.add(child1)
//    app.run(rootView);
}