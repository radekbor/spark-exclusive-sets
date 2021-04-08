package dev.borowiecki.sets

import com.risksense.ipaddr.IpNetwork

object KeyGenerator {

  def usingIpMask(bits: Int)(ip: RawIp) = {
    val net = IpNetwork(ip, 32 - bits)
    net.first
  }
}
