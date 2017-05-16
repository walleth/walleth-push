package wallethpush


fun main(args: Array<String>) {

    if (!configFile.exists()) {
        println("config file $configFile does not exist - exitting")
        return
    }

    startWebServer()
    watchChain()

}


