package edu.stevens.cs522.chatserver.entities;

import androidx.room.TypeConverter;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.InetAddressUtils;

// TODO complete this class (use InetAddressUtils)
public class InetAddressConverter {
    @TypeConverter
    public static InetAddress fromAddressp(String ipString) {
        return ipString == null ? null : InetAddressUtils.fromString(ipString);
    }

    @TypeConverter
    public static String addressToString(InetAddress address) {
        return address == null ? null : InetAddressUtils.toIpAddress(address);
    }
}
