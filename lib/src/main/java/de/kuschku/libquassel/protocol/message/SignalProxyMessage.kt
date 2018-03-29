package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.value
import org.threeten.bp.Instant

sealed class SignalProxyMessage {
  class SyncMessage(val className: String, val objectName: String, val slotName: String,
                    val params: QVariantList) : SignalProxyMessage() {
    override fun toString(): String {
      return "SyncMessage::$className:$objectName:$slotName"
    }
  }

  class RpcCall(val slotName: String, val params: QVariantList) : SignalProxyMessage() {
    override fun toString(): String {
      return "RpcCall::$slotName"
    }
  }

  class InitRequest(val className: String, val objectName: String) : SignalProxyMessage() {
    override fun toString(): String {
      return "InitRequest::$className:$objectName"
    }
  }

  class InitData(val className: String, val objectName: String, val initData: QVariantMap) :
    SignalProxyMessage() {
    override fun toString(): String {
      return "InitData::$className:$objectName"
    }
  }

  class HeartBeat(val timestamp: Instant) : SignalProxyMessage() {
    override fun toString(): String {
      return "HeartBeat::$timestamp"
    }
  }

  class HeartBeatReply(val timestamp: Instant) : SignalProxyMessage() {
    override fun toString(): String {
      return "HeartBeatReply::$timestamp"
    }
  }

  companion object :
    SignalProxyMessageSerializer<SignalProxyMessage> {
    override fun serialize(data: SignalProxyMessage) = when (data) {
      is SyncMessage    -> SyncMessageSerializer.serialize(data)
      is RpcCall        -> RpcCallSerializer.serialize(data)
      is InitRequest    -> InitRequestSerializer.serialize(data)
      is InitData       -> InitDataSerializer.serialize(data)
      is HeartBeat      -> HeartBeatSerializer.serialize(data)
      is HeartBeatReply -> HeartBeatReplySerializer.serialize(data)
    }

    override fun deserialize(data: QVariantList): SignalProxyMessage {
      val type = data.first().value(-1)
      return when (RequestType.of(type)) {
        RequestType.Sync           -> SyncMessageSerializer.deserialize(data.drop(1))
        RequestType.RpcCall        -> RpcCallSerializer.deserialize(data.drop(1))
        RequestType.InitRequest    -> InitRequestSerializer.deserialize(data.drop(1))
        RequestType.InitData       -> InitDataSerializer.deserialize(data.drop(1))
        RequestType.HeartBeat      -> HeartBeatSerializer.deserialize(data.drop(1))
        RequestType.HeartBeatReply -> HeartBeatReplySerializer.deserialize(data.drop(1))
        else                       -> throw IllegalArgumentException("Invalid MsgType: $type")
      }
    }
  }
}
