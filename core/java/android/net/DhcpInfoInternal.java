/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.net;

import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * A simple object for retrieving the results of a DHCP request.
 * Replaces (internally) the IPv4-only DhcpInfo class.
 * @hide
 */
public class DhcpInfoInternal {
    private final static String TAG = "DhcpInfoInternal";
    public String ipAddress;
    public String gateway;
    public int prefixLength;

    public String dns1;
    public String dns2;

    public String serverAddress;
    public int leaseDuration;

    public DhcpInfoInternal() {
    }

    private int convertToInt(String addr) {
        try {
            InetAddress inetAddress = NetworkUtils.numericToInetAddress(addr);
            if (inetAddress instanceof Inet4Address) {
                return NetworkUtils.inetAddressToInt(inetAddress);
            }
        } catch (IllegalArgumentException e) {}
        return 0;
    }

    public DhcpInfo makeDhcpInfo() {
        DhcpInfo info = new DhcpInfo();
        info.ipAddress = convertToInt(ipAddress);
        info.gateway = convertToInt(gateway);
        try {
            InetAddress inetAddress = NetworkUtils.numericToInetAddress(ipAddress);
            info.netmask = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        } catch (IllegalArgumentException e) {}
        info.dns1 = convertToInt(dns1);
        info.dns2 = convertToInt(dns2);
        info.serverAddress = convertToInt(serverAddress);
        info.leaseDuration = leaseDuration;
        return info;
    }

    public LinkAddress makeLinkAddress() {
        if (TextUtils.isEmpty(ipAddress)) {
            Log.e(TAG, "makeLinkAddress with empty ipAddress");
            return null;
        }
        return new LinkAddress(NetworkUtils.numericToInetAddress(ipAddress), prefixLength);
    }

    public LinkProperties makeLinkProperties() {
        LinkProperties p = new LinkProperties();
        p.addLinkAddress(makeLinkAddress());
        if (TextUtils.isEmpty(gateway) == false) {
            p.setGateway(NetworkUtils.numericToInetAddress(gateway));
        } else {
            Log.e(TAG, "makeLinkProperties with empty gateway!");
        }
        if (TextUtils.isEmpty(dns1) == false) {
            p.addDns(NetworkUtils.numericToInetAddress(dns1));
        } else {
            Log.e(TAG, "makeLinkProperties with empty dns1!");
        }
        if (TextUtils.isEmpty(dns2) == false) {
            p.addDns(NetworkUtils.numericToInetAddress(dns2));
        } else {
            Log.e(TAG, "makeLinkProperties with empty dns2!");
        }
        return p;
    }

    public String toString() {
        return "addr: " + ipAddress + "/" + prefixLength +
                " gateway: " + gateway +
                " dns: " + dns1 + "," + dns2 +
                " dhcpServer: " + serverAddress +
                " leaseDuration: " + leaseDuration;
    }
}
