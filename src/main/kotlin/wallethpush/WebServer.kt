package wallethpush

import kontinuum.ConfigProvider
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.embeddedNettyServer
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing


fun startWebServer() {

    embeddedNettyServer(ConfigProvider.config.port) {

        routing {
            post("/") {
                val requestContent = call.request.content[String::class]

                val pushMapping = pushConfigRequestPayloadAdapter.fromJson(requestContent)

                if (pushMapping != null && pushMapping.uid.isNotBlank() && pushMapping.pushToken.isNotBlank()) {
                    println("registered mapping: " + pushMapping)
                    pushMappingStore.setPushMapping(pushMapping)
                    call.respondText("OK", ContentType.Text.Html)
                } else {
                    println("ERROR: invalid request")
                    call.respondText("ERROR", ContentType.Text.Html)
                }
            }
            get("/") {
                call.respondText("WALLETH push", ContentType.Text.Html)
            }
        }
    }.start(wait = false)

}

