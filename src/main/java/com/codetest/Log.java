package com.codetest;

/**
 * @Author 金田燎弥
 */

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public interface Log {
    LogItem next();
}

/** アドレスとプレフィックスをまとめたクラス
 * 追加ライブラリでうまくいく方法もいくつかはあるかと思うが
 * 今回はそれ用に作成した.
 */
final class AddressPrefix{
    private final Inet4Address address;
    private final int prefix;
    AddressPrefix(Inet4Address address, int prefix){
        this.address = address;
        this.prefix = prefix;
    }
    public int getSubnet(){
        // 論理演算
        int subnet = ((1 << (this.prefix))-1)<<(32-this.prefix);
        byte[] bytes = this.address.getAddress();
        int addressInt = ((((int)bytes[0])&0xff)<<24)
                + ((((int)bytes[1])&0xff)<<16)
                + ((((int)bytes[2])&0xff)<<8)
                + ((((int)bytes[3])&0xff));
        return addressInt & subnet;
    }
    // わかりやすく表示する用
    public static String subnet2Address(int subnet){
        String[] ss = new String[4];
        ss[0] = String.valueOf((subnet>>24)&255);
        ss[1] = String.valueOf((subnet>>16)&255);
        ss[2] = String.valueOf((subnet>>8)&255);
        ss[3] = String.valueOf((subnet)&255);
        return String.join(".", ss);
    }
    // ハッシュとして使う用
    @Override
    public boolean equals(Object obj){
        if (obj instanceof AddressPrefix) {
            AddressPrefix that = (AddressPrefix) obj;
            return this.address.equals(that.address) && this.prefix == that.prefix;
        }else{
            return false;
        }

    }
    // ハッシュとして使う用
    @Override
    public int hashCode() {
        return Objects.hash(address.hashCode(), prefix);
    }
    // デバッグ用
    @Override
    public String toString(){
        return this.address.getHostAddress() + "/" + this.prefix;
    }
}

/**
 *
 */
class LogItem {
    static final String FORMAT = "yyyyMMddHHmmss";
    private LocalDateTime date;
    private AddressPrefix addressPrefix;
    private int prefix;
    private int responseTime;
    private boolean isTimeOut;
    private boolean isBrokenLog = false;

    LogItem(String strDate, String strAddress, String response_time) {
        try {
            // 時間を変換
            this.date = LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(FORMAT));
            // アドレスを変換
            String[] tmp = strAddress.split("/");
            Inet4Address address = (Inet4Address) InetAddress.getByName(tmp[0]);
            this.addressPrefix = new AddressPrefix(address, Integer.parseInt(tmp[1]));
            // レスポンスタイムを変換
            this.isTimeOut = response_time.equals("-");
            if(this.isTimeOut){
                this.responseTime = -1;
            }else{
                this.responseTime = Integer.parseInt(response_time);
            }
        } catch (DateTimeParseException
                 | UnknownHostException
                 | ArrayIndexOutOfBoundsException e) {
            this.isBrokenLog = true;
            // e.printStackTrace();
        }
    }
    // getter and setter
    public AddressPrefix getAddressPrefix(){
        return this.addressPrefix;
    }
    public LocalDateTime getDate(){
        return this.date;
    }
    public int getResponseTime(){
        return this.responseTime;
    }
    public boolean isTimeOut(){
        return this.isTimeOut;
    }
    public boolean isBrokenLog(){
        return this.isBrokenLog;
    }

    @Override
    public String toString()
    {
        return String.join(",",
                ""+this.date,
                ""+this.addressPrefix,
                ""+(this.isTimeOut?"-": this.responseTime)
        );
    }
}
