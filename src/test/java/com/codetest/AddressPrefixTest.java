package com.codetest;

/**
 * @Author 金田燎弥
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class AddressPrefixTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getSubnet() throws UnknownHostException {
        String[] tmp1 = "12.34.56.78/16".split("/");
        Inet4Address address1 = (Inet4Address) InetAddress.getByName(tmp1[0]);
        AddressPrefix ap1 = new AddressPrefix(address1, Integer.parseInt(tmp1[1]));
        String[] tmp2 = "12.34.54.32/16".split("/");
        Inet4Address address2 = (Inet4Address) InetAddress.getByName(tmp2[0]);
        AddressPrefix ap2 = new AddressPrefix(address2, Integer.parseInt(tmp2[1]));
        Assertions.assertEquals((12<<24)+(34<<16), ap2.getSubnet());
        Assertions.assertEquals((12<<24)+(34<<16), ap1.getSubnet());
    }

    @Test
    void subnet2Address() throws UnknownHostException {
        String[] tmp1 = "12.34.56.78/16".split("/");
        Inet4Address address1 = (Inet4Address) InetAddress.getByName(tmp1[0]);
        AddressPrefix ap1 = new AddressPrefix(address1, Integer.parseInt(tmp1[1]));
        String[] tmp2 = "12.34.54.32/16".split("/");
        Inet4Address address2 = (Inet4Address) InetAddress.getByName(tmp2[0]);
        AddressPrefix ap2 = new AddressPrefix(address2, Integer.parseInt(tmp2[1]));
        String aps1 = AddressPrefix.subnet2Address(ap1.getSubnet());
        String aps2 = AddressPrefix.subnet2Address(ap2.getSubnet());
        Assertions.assertEquals("12.34.0.0", aps1);
        Assertions.assertEquals("12.34.0.0", aps2);
    }

    @Test
    void testEquals() throws UnknownHostException {
        String[] tmp1 = "12.34.56.78/16".split("/");
        Inet4Address address1 = (Inet4Address) InetAddress.getByName(tmp1[0]);
        AddressPrefix ap1 = new AddressPrefix(address1, Integer.parseInt(tmp1[1]));
        String[] tmp2 = "12.34.54.32/16".split("/");
        Inet4Address address2 = (Inet4Address) InetAddress.getByName(tmp2[0]);
        AddressPrefix ap2 = new AddressPrefix(address2, Integer.parseInt(tmp2[1]));
        String[] tmp3 = "12.34.56.78/16".split("/");
        Inet4Address address3 = (Inet4Address) InetAddress.getByName(tmp3[0]);
        AddressPrefix ap3 = new AddressPrefix(address3, Integer.parseInt(tmp3[1]));
        Assertions.assertNotEquals(ap1, ap2);
        Assertions.assertEquals(ap1, ap3);
    }

    @Test
    void testHashCode() throws UnknownHostException {
        String[] tmp1 = "12.34.56.78/16".split("/");
        Inet4Address address1 = (Inet4Address) InetAddress.getByName(tmp1[0]);
        AddressPrefix ap1 = new AddressPrefix(address1, Integer.parseInt(tmp1[1]));
        String[] tmp2 = "12.34.54.32/16".split("/");
        Inet4Address address2 = (Inet4Address) InetAddress.getByName(tmp2[0]);
        AddressPrefix ap2 = new AddressPrefix(address2, Integer.parseInt(tmp2[1]));
        String[] tmp3 = "12.34.56.78/16".split("/");
        Inet4Address address3 = (Inet4Address) InetAddress.getByName(tmp3[0]);
        AddressPrefix ap3 = new AddressPrefix(address3, Integer.parseInt(tmp3[1]));
        Assertions.assertNotEquals(ap1.hashCode(), ap2.hashCode());
        Assertions.assertEquals(ap1.hashCode(), ap3.hashCode());
    }
}