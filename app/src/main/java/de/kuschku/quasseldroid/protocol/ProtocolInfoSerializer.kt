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

package de.kuschku.quasseldroid.protocol

import de.kuschku.quasseldroid.ProtocolInfo
import java.nio.ByteBuffer

object ProtocolInfoSerializer : Serializer<ProtocolInfo> {
  override fun serialize(buffer: ChainedByteBuffer, data: ProtocolInfo) {
      UByteSerializer.serialize(buffer, data.flags)
      UShortSerializer.serialize(buffer, data.data)
      UByteSerializer.serialize(buffer, data.version)
  }

  override fun deserialize(buffer: ByteBuffer): ProtocolInfo {
    return ProtocolInfo(
            UByteSerializer.deserialize(buffer),
            UShortSerializer.deserialize(buffer),
            UByteSerializer.deserialize(buffer)
    )
  }
}