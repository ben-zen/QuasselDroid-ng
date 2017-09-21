package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.syncables.interfaces.IIrcChannel
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.helpers.getOr
import java.nio.charset.Charset

class IrcChannel(
  name: String,
  network: Network,
  proxy: SignalProxy
) : SyncableObject(proxy, "IrcChannel"), IIrcChannel {
  override fun init() {
    renameObject("${network().networkId()}/${name()}")
  }

  override fun toVariantMap(): QVariantMap = mapOf(
    "ChanModes" to QVariant_(initChanModes(), Type.QVariantMap),
    "UserModes" to QVariant_(initUserModes(), Type.QVariantMap)
  ) + initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetChanModes(properties["ChanModes"].valueOr(::emptyMap))
    initSetUserModes(properties["UserModes"].valueOr(::emptyMap))
    initSetProperties(properties)
  }

  override fun initChanModes(): QVariantMap = mapOf(
    "A" to QVariant_(_A_channelModes.entries.map { (key, value) ->
      key to QVariant_(value.toList(), Type.QStringList)
    }, Type.QVariantMap),
    "B" to QVariant_(_B_channelModes.entries.map { (key, value) ->
      key to QVariant_(value, Type.QString)
    }, Type.QVariantMap),
    "C" to QVariant_(_C_channelModes.entries.map { (key, value) ->
      key to QVariant_(value, Type.QString)
    }, Type.QVariantMap),
    "D" to QVariant_(_D_channelModes.joinToString(), Type.QString)
  )

  override fun initUserModes(): QVariantMap = _userModes.entries.map { (key, value) ->
    key.nick() to QVariant_(value, Type.QString)
  }.toMap()

  override fun initProperties(): QVariantMap = mapOf(
    "name" to QVariant_(name(), Type.QString),
    "topic" to QVariant_(topic(), Type.QString),
    "password" to QVariant_(password(), Type.QString),
    "encrypted" to QVariant_(encrypted(), Type.Bool)
  )

  override fun initSetChanModes(chanModes: QVariantMap) {
    chanModes["A"].valueOr<Map<String, QVariant_>>(::emptyMap).forEach { (key, variant) ->
      _A_channelModes[key.toCharArray().first()] =
        variant.valueOr<QStringList>(::emptyList).filterNotNull().toMutableSet()
    }
    chanModes["B"].valueOr<Map<String, QVariant_>>(::emptyMap).forEach { (key, variant) ->
      _B_channelModes[key.toCharArray().first()] = variant.value("")
    }
    chanModes["C"].valueOr<Map<String, QVariant_>>(::emptyMap).forEach { (key, variant) ->
      _C_channelModes[key.toCharArray().first()] = variant.value("")
    }
    _D_channelModes = chanModes["D"].value("").toCharArray().toMutableSet()
  }

  override fun initSetUserModes(usermodes: QVariantMap) {
    _userModes.putAll(usermodes.entries.map { (key, value) ->
      network().newIrcUser(key) to value.value("")
    }.toMap())
  }

  override fun initSetProperties(properties: QVariantMap) {
    setTopic(properties["topic"].value(topic()))
    setPassword(properties["password"].value(password()))
    setEncrypted(properties["encrypted"].value(encrypted()))
  }

  fun isKnownUser(ircUser: IrcUser): Boolean {
    return _userModes.contains(ircUser)
  }

  fun isValidChannelUserMode(mode: String): Boolean {
    return mode.length <= 1
  }

  fun name() = _name
  fun topic() = _topic
  fun password() = _password
  fun encrypted() = _encrypted
  fun network() = _network
  fun ircUsers() = _userModes.keys
  fun userModes(ircUser: IrcUser) = _userModes.getOr(ircUser, "")
  fun userModes(nick: String) = network().ircUser(nick)?.let { userModes(it) } ?: ""
  fun hasMode(mode: Char) = when (network().channelModeType(mode)) {
    INetwork.ChannelModeType.A_CHANMODE ->
      _A_channelModes.contains(mode)
    INetwork.ChannelModeType.B_CHANMODE ->
      _B_channelModes.contains(mode)
    INetwork.ChannelModeType.C_CHANMODE ->
      _C_channelModes.contains(mode)
    INetwork.ChannelModeType.D_CHANMODE ->
      _D_channelModes.contains(mode)
    else                                ->
      false
  }

  fun modeValue(mode: Char) = when (network().channelModeType(mode)) {
    INetwork.ChannelModeType.B_CHANMODE ->
      _B_channelModes.getOr(mode, "")
    INetwork.ChannelModeType.C_CHANMODE ->
      _C_channelModes.getOr(mode, "")
    else                                ->
      ""
  }

  fun modeValueList(mode: Char): Set<String> = when (network().channelModeType(mode)) {
    INetwork.ChannelModeType.A_CHANMODE ->
      _A_channelModes.getOrElse(mode, ::emptySet)
    else                                ->
      emptySet()
  }

  fun channelModeString(): String {
    val modeString = StringBuffer(_D_channelModes.joinToString())
    val params = mutableListOf<String>()
    _C_channelModes.entries.forEach { (key, value) ->
      modeString.append(key)
      params.add(value)
    }
    _B_channelModes.entries.forEach { (key, value) ->
      modeString.append(key)
      params.add(value)
    }
    return if (modeString.isBlank()) {
      ""
    } else {
      "+$modeString ${params.joinToString(" ")}"
    }
  }

  fun codecForEncoding() = _codecForEncoding
  fun codecForDecoding() = _codecForDecoding
  fun setCodecForEncoding(codecName: String) = setCodecForEncoding(Charset.forName(codecName))
  fun setCodecForEncoding(codec: Charset) {
    _codecForEncoding = codec
  }

  fun setCodecForDecoding(codecName: String) = setCodecForDecoding(Charset.forName(codecName))
  fun setCodecForDecoding(codec: Charset) {
    _codecForDecoding = codec
  }

  override fun setTopic(topic: String) {
    if (_topic == topic)
      return
    _topic = topic
    super.setTopic(topic)
  }

  override fun setPassword(password: String) {
    if (_password == password)
      return
    _password = password
    super.setPassword(password)
  }

  override fun setEncrypted(encrypted: Boolean) {
    if (_encrypted == encrypted)
      return
    _encrypted = encrypted
    super.setEncrypted(encrypted)
  }

  override fun joinIrcUsers(nicks: QStringList, modes: QStringList) {
    val (rawUsers, rawModes) = nicks.zip(modes)
      .map { network().ircUser(it.first) to it.second }
      .filter { it.first != null }
      .map { Pair(it.first!!, it.second ?: "") }.unzip()
    joinIrcUsersInternal(rawUsers, rawModes)
  }

  private fun joinIrcUsersInternal(rawUsers: List<IrcUser>, rawModes: List<String>) {
    val users = rawUsers.zip(rawModes)
    val newNicks = users.filter { !_userModes.contains(it.first) }
    val oldNicks = users.filter { _userModes.contains(it.first) }
    for ((user, modes) in oldNicks) {
      modes.forEach { mode ->
        addUserMode(user, mode)
      }
    }
    for ((user, modes) in newNicks) {
      _userModes[user] = modes
      user.joinChannel(this, true)
    }
    if (newNicks.isNotEmpty())
      super.joinIrcUsers(
        newNicks.map(Pair<IrcUser, String>::first).map(IrcUser::nick),
        newNicks.map(Pair<IrcUser, String>::second)
      )
  }

  override fun joinIrcUser(ircuser: IrcUser) {
    joinIrcUsersInternal(listOf(ircuser), listOf(""))
  }

  override fun part(ircuser: IrcUser?) {
    if (ircuser == null)
      return
    if (!isKnownUser(ircuser))
      return
    _userModes.remove(ircuser)
    ircuser.partChannel(this)
    if (network().isMe(ircuser) || _userModes.isEmpty()) {
      for (user in _userModes.keys) {
        user.partChannel(this)
      }
      _userModes.clear()
      network().removeIrcChannel(this)
      proxy.stopSynchronize(this)
    }
    super.part(ircuser)
  }

  override fun part(nick: String) {
    part(network().ircUser(nick))
  }

  override fun setUserModes(ircuser: IrcUser?, modes: String) {
    if (ircuser == null || !isKnownUser(ircuser))
      return
    _userModes[ircuser] = modes
    super.setUserModes(ircuser.nick(), modes)
  }

  override fun setUserModes(nick: String, modes: String) {
    setUserModes(network().ircUser(nick), modes)
  }

  fun addUserMode(ircuser: IrcUser, mode: Char) {
    super.addUserMode(ircuser, Character.toString(mode))
  }

  override fun addUserMode(ircuser: IrcUser?, mode: String) {
    if (ircuser == null || !isKnownUser(ircuser) || !isValidChannelUserMode(mode))
      return
    if (_userModes.getOr(ircuser, "").contains(mode, ignoreCase = true))
      return
    _userModes[ircuser] = _userModes.getOr(ircuser, "") + mode
    super.addUserMode(ircuser.nick(), mode)
  }

  override fun addUserMode(nick: String, mode: String) {
    addUserMode(network().ircUser(nick), mode)
  }

  override fun removeUserMode(ircuser: IrcUser?, mode: String) {
    if (ircuser == null || !isKnownUser(ircuser) || !isValidChannelUserMode(mode))
      return
    if (_userModes.getOr(ircuser, "").contains(mode, ignoreCase = true))
      return
    _userModes[ircuser] = _userModes.getOr(ircuser, "")
      .replace(mode, "", ignoreCase = true)
    super.addUserMode(ircuser.nick(), mode)
  }

  override fun removeUserMode(nick: String, mode: String) {
    removeUserMode(network().ircUser(nick), mode)
  }

  override fun addChannelMode(mode: Char, value: String) {
    when (network().channelModeType(mode)) {
      INetwork.ChannelModeType.A_CHANMODE     ->
        _A_channelModes.getOrPut(mode, ::mutableSetOf).add(value)
      INetwork.ChannelModeType.B_CHANMODE     ->
        _B_channelModes[mode] = value
      INetwork.ChannelModeType.C_CHANMODE     ->
        _C_channelModes[mode] = value
      INetwork.ChannelModeType.D_CHANMODE     ->
        _D_channelModes.add(mode)
      INetwork.ChannelModeType.NOT_A_CHANMODE ->
        throw IllegalArgumentException("Received invalid channel mode: $mode $value")
    }
    super.addChannelMode(mode, value)
  }

  override fun removeChannelMode(mode: Char, value: String) {
    when (network().channelModeType(mode)) {
      INetwork.ChannelModeType.A_CHANMODE     ->
        _A_channelModes.getOrPut(mode, ::mutableSetOf).remove(value)
      INetwork.ChannelModeType.B_CHANMODE     ->
        _B_channelModes.remove(mode)
      INetwork.ChannelModeType.C_CHANMODE     ->
        _C_channelModes.remove(mode)
      INetwork.ChannelModeType.D_CHANMODE     ->
        _D_channelModes.remove(mode)
      INetwork.ChannelModeType.NOT_A_CHANMODE ->
        throw IllegalArgumentException("Received invalid channel mode: $mode $value")
    }
    super.removeChannelMode(mode, value)
  }

  private var _name: String = name
  private var _topic: String = ""
  private var _password: String = ""
  private var _encrypted: Boolean = false
  private var _userModes: MutableMap<IrcUser, String> = mutableMapOf()
  private var _network: Network = network
  private var _codecForEncoding: Charset? = null
  private var _codecForDecoding: Charset? = null
  private var _A_channelModes: MutableMap<Char, MutableSet<String>> = mutableMapOf()
  private var _B_channelModes: MutableMap<Char, String> = mutableMapOf()
  private var _C_channelModes: MutableMap<Char, String> = mutableMapOf()
  private var _D_channelModes: MutableSet<Char> = mutableSetOf()
}