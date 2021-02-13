/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.justjanne.libquassel.protocol.serializers.handshake

import de.justjanne.libquassel.protocol.messages.handshake.CoreSetupAck
import de.justjanne.libquassel.protocol.variant.QVariantMap
import de.justjanne.libquassel.protocol.variant.QtType
import de.justjanne.libquassel.protocol.variant.qVariant

object CoreSetupAckSerializer : HandshakeSerializer<CoreSetupAck> {
  override val type: String = "CoreSetupAck"
  override val javaType: Class<out CoreSetupAck> = CoreSetupAck::class.java

  override fun serialize(data: CoreSetupAck) = mapOf(
    "MsgType" to qVariant(type, QtType.QString)
  )

  override fun deserialize(data: QVariantMap) = CoreSetupAck
}