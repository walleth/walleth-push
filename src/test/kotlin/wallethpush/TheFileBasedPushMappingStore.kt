package  wallethpush

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wallethpush.model.PushMapping
import wallethpush.logic.push_mapping.FileBasedPushMappingStore

class TheFileBasedPushMappingStore {

    @Test
    fun emptyWorks() {
        val tested = FileBasedPushMappingStore()

        assertThat(tested.getTokensForAddress("0xF00")).isEmpty()
    }


    @Test
    fun singleWorks() {
        val tested = FileBasedPushMappingStore()

        tested.setPushMapping(PushMapping("BAR","TOKENPROBE", listOf("0xF00")))

        assertThat(tested.getTokensForAddress("0xF00")).containsExactly("TOKENPROBE")
    }


    @Test
    fun multipleWorks() {
        val tested = FileBasedPushMappingStore()

        tested.setPushMapping(PushMapping("BAR","TOKENPROBE", listOf("0xF00")))
        tested.setPushMapping(PushMapping("BAR","TOKENPROBE2", listOf("0xF00")))

        assertThat(tested.getTokensForAddress("0xF00")).containsExactly("TOKENPROBE","TOKENPROBE2")
    }

}