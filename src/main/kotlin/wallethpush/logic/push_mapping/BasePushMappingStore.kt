package wallethpush.logic.push_mapping

import wallethpush.model.PushMapping

open class BasePushMappingStore : PushMappingStore {

    private var addressToUIDMap = mutableMapOf<String, MutableList<String>>()
    private val uidToTokenMap = mutableMapOf<String, String>()

    override fun getTokensForAddress(address: String) = if (addressToUIDMap.containsKey(address)) {
        addressToUIDMap[address]!!.map { uidToTokenMap[it]!! }
    } else {
        emptyList()
    }

    override fun setPushMapping(pushMapping: PushMapping) {

        addressToUIDMap = addressToUIDMap.map {
            it.key to it.value.filter { uid -> uid != pushMapping.uid }.toMutableList()
        }.toMap().toMutableMap()

        setPushMappingInternal(pushMapping)
    }

    protected fun setPushMappingInternal(pushMapping: PushMapping) {
        uidToTokenMap[pushMapping.uid] = pushMapping.pushToken

        pushMapping.addresses.forEach {
            if (addressToUIDMap[it] != null) {
                addressToUIDMap[it]!!.add(pushMapping.uid)
            } else {
                addressToUIDMap[it] = mutableListOf(pushMapping.uid)
            }
        }
    }
}