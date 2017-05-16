package wallethpush

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.embeddedNettyServer
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing


fun startWebServer() {

    embeddedNettyServer(9443) {

        routing {

            get("/") {
                call.respondText("WALLETH push", ContentType.Text.Html)
            }
        }
    }.start(wait = false)

}

