package  wallethpush

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wallethpush.model.PushMessage
import wallethpush.model.PushMessageData

class ThePushMessageAdapter {

    val moshi = Moshi.Builder().build()
    val pushMessageAdapter: JsonAdapter<PushMessage> = moshi.adapter(PushMessage::class.java)

    @Test
    fun adapterWorks() {
        val probe =
                """
                {
                 "data": { "address": "0xFOO" },
                 "to" : "TOFOO"
                }
                """

        val tested = pushMessageAdapter.fromJson(probe)

        assertThat(tested!!.data.address).isEqualTo("0xFOO")
        assertThat(tested.to).isEqualTo("TOFOO")
    }

    @Test
    fun survivesRoundTrip() {
        val probe = PushMessage(to = "YO", data = PushMessageData("0xDATA"))

        val string: String = pushMessageAdapter.toJson(probe)

        val tested = pushMessageAdapter.fromJson(string)

        assertThat(tested).isEqualTo(probe)
    }

}