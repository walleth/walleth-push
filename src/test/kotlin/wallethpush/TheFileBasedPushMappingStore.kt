package  wallethpush

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wallethpush.logic.push_mapping.FileBasedPushMappingStore
import wallethpush.model.PushMapping

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
        tested.setPushMapping(PushMapping("BAR2","TOKENPROBE2", listOf("0xF00")))

        assertThat(tested.getTokensForAddress("0xF00")).containsExactly("TOKENPROBE","TOKENPROBE2")
    }

    @Test
    fun removingWorks() {
        val tested = FileBasedPushMappingStore()

        tested.setPushMapping(PushMapping("BAR","TOKENPROBE", listOf("0xF00")))
        tested.setPushMapping(PushMapping("BAR2","TOKENPROBE2", listOf("0xF00")))
        tested.setPushMapping(PushMapping("BAR2","TOKENPROBE2", listOf("0xF01")))

        assertThat(tested.getTokensForAddress("0xF00")).containsExactly("TOKENPROBE")
    }

}