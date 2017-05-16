package wallethpush

import wallethpush.logic.startWebServer


fun main(args: Array<String>) {

    if (!configFile.exists()) {
        println("config file $configFile does not exist - exitting")
        return
    }

    startWebServer()
    watchChain()

}


