package com.codetest;

/**
 * @Author 金田燎弥
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class Main {
    final static String MESSAGE_HUKKYU01 = "%s が復旧しました. 復旧にかかった時間は　%s です.";
    final static String MESSAGE_NOBERAK = "%s に先ほど接続できませんでしたが, %s 回の送信で帰ってきたため壊れていません.";
    final static String MESSAGE_HUKKYU02 = "%s が復旧しました. 復旧にかかった時間は　%s です. なお, 復旧までに送られた ping の回数は %s 回です.";
    final static String MESSAGE_KAHUKA03= "%s の過負荷を検出しました.";
    final static String MESSAGE_KAHUKA_HUKKYU03 = "" +
            "%s が過負荷状態から解消されました. 復旧にかかった時間は　%s です. なお, 復旧までに送られた ping の回数は %s 回です. " +
            "平均応答時間が %s ミリ秒に落ち着きました. ";
    final static String MESSAGE_HUKKYU04 = "サブネット %s が復旧しました. 復旧にかかった時間は　%s です. なお, 復旧までに送られた ping の回数は %s 回です.";

    // 設問 2 以降で使用
    private static class ErrorInfo{
        private final ArrayList<LogItem> itemList;
        private final LocalDateTime firstMissTime;
        private int missCount;
        ErrorInfo(LogItem firstItem){
            this.itemList = new ArrayList<>();
            this.firstMissTime = firstItem.getDate();
            this.add(firstItem);
        }
        public void add(LogItem logItem){
            this.itemList.add(logItem);
            this.missCount++;
        }
        // getter
        public int getMissCount(){
            return missCount;
        }
    }
    // 設問 3 以降で使用
    private static class PingTimeInfo{
        private final LinkedList<Integer> pingTimeList;
        private int sum; // 計算時間短縮のために変数に保存しておく
        private final int maxCount;
        public PingTimeInfo(int maxCount){
            this.maxCount = maxCount;
            this.pingTimeList = new LinkedList<>();
            for(int i=0;i<maxCount;i++){this.pingTimeList.push(0);}
            sum = 0;
        }
        public int newPing(int ping){
            this.pingTimeList.offer(ping);
            this.sum = this.sum - this.pingTimeList.pop() + ping;
            return this.sum / maxCount;
        }
    }

    public static void task00(){
        Log sampleLog = new LogByFile("/testcase10.txt");
        LogItem logItem;
        while((logItem=sampleLog.next())!=null){
            if(logItem.isBrokenLog()) continue;
            System.out.println(logItem);
            System.out.println(
                    AddressPrefix.subnet2Address(logItem.getAddressPrefix().getSubnet())
            );
        }
        System.out.println("end.");
    }
    public static long getElapsedTime(LocalDateTime _from, LocalDateTime _to){
        return Duration.between(_from, _to).toSeconds();// 期間分の時間を取得する
    }

    public static void task01(String fileName){
        // 今回はファイルからログを読み取るのでLogByFileクラスを呼び出す.
        Log sampleLog = new LogByFile(fileName);
        // タイムアウトをおこしたアドレスを保管する
        Map<AddressPrefix, LocalDateTime> timeOutAddress = new HashMap<>();
        //
        // ログの中の一つのパーツをLogItemとしている.
        LogItem logItem;
        // while内で各ログに対する処理をしている.
        while((logItem=sampleLog.next())!=null){
            // もし仮にログが壊れていたらパスする.
            if(logItem.isBrokenLog()) continue;
            // ここから先はログが壊れていないことが前提
            AddressPrefix address = logItem.getAddressPrefix();// アドレス
            boolean isTimeOut = logItem.isTimeOut(); // TimeOutしたかどうか
            boolean beforeTimeOut = timeOutAddress.containsKey(address); // TimeOutしているかどうか
            // 以前からタイムアウトしておらず現在もタイムアウトしていないとき(理想)
            if(!beforeTimeOut && !isTimeOut){}
            // 以前タイムアウトしていて今回タイムアウトを回避した時
            // この時はタイムアウトした時間を数えて、それを表示する
            // ついでにMapを更新する
            else if(beforeTimeOut && !isTimeOut){
                LocalDateTime timeOutFrom = timeOutAddress.get(address);
                LocalDateTime timeNew = logItem.getDate();
                long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                timeOutAddress.remove(address);
                System.out.printf((MESSAGE_HUKKYU01) + "%n", address.toString(), elapsedTime+"秒");
            }
            // 以前はタイムアウトしておらず今回タイムアウトした時 (仕方ない)
            // この時はタイムアウトをMapに記録する
            else if(!beforeTimeOut && isTimeOut){
                timeOutAddress.put(address, logItem.getDate());
            }
            // 以前からタイムアウトしていて現在もタイムアウト時（重症）
            else if(beforeTimeOut && isTimeOut){}// 今回の設問では何もしない
        }
        System.out.println("end.");
    }
    public static void task02(String fileName, int N){
        // 今回はファイルからログを読み取るのでLogByFileクラスを呼び出す.
        Log sampleLog = new LogByFile(fileName);
        // タイムアウトをおこしたアドレスを保管する
        // なお、設問1からの変化として、タイムアウトを起こした瞬間の時間ではなく
        // タイムアウトを起こしている間のログを集計している
        Map<AddressPrefix, ErrorInfo> timeOutAddressMap = new HashMap<>();
        //
        // ログの中の一つのパーツをLogItemとしている.
        LogItem logItem;
        // while内で各ログに対する処理をしている.
        while((logItem=sampleLog.next())!=null){
            // もし仮にログが壊れていたらパスする.
            if(logItem.isBrokenLog()) continue;
            // ここから先はログが壊れていないことが前提
            AddressPrefix address = logItem.getAddressPrefix();// アドレス
            boolean isTimeOut = logItem.isTimeOut(); // TimeOutしたかどうか
            boolean beforeTimeOut = timeOutAddressMap.containsKey(address); // TimeOutしているかどうか
            // 以前からタイムアウトしておらず現在もタイムアウトしていないとき(理想)
            if(!beforeTimeOut && !isTimeOut){} // 特に何もせず
            // 以前タイムアウトしていて今回タイムアウトを回避した時
            // この時はタイムアウトした回数と時間を数えて、それを表示する
            // ついでにMapを更新する
            else if(beforeTimeOut && !isTimeOut){
                // Map の更新
                ErrorInfo ei = timeOutAddressMap.get(address);
                timeOutAddressMap.remove(address);
                //
                int missCount = ei.getMissCount();
                if(missCount>=N){
                    LocalDateTime timeOutFrom = ei.firstMissTime;
                    LocalDateTime timeNew = logItem.getDate();
                    long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                    System.out.printf((MESSAGE_HUKKYU02) + "%n", address.toString(), elapsedTime+" 秒", missCount);
                }else{
                    // System.out.println(String.format(MESSAGE_NOBERAK, address.toString(), missCount));
                }
            }
            // 以前はタイムアウトしておらず今回タイムアウトした時 (仕方ない)
            // この時は初めてのタイムアウトとしてMapに記録する
            else if(!beforeTimeOut && isTimeOut){
                timeOutAddressMap.put(address, new ErrorInfo(logItem));
            }
            // 以前からタイムアウトしていて現在もタイムアウト時（重症）
            // この際にはこのアドレスのタイムアウト履歴を更新する,
            else if(beforeTimeOut && isTimeOut){
                timeOutAddressMap.get(address).add(logItem);
            }
        }
        System.out.println("end.");
    }
    public static void task03(String fileName, int N, int m, int t){
        // 変数名がややこしいので変える
        // 今回はファイルからログを読み取るのでLogByFileクラスを呼び出す.
        Log sampleLog = new LogByFile(fileName);
        // タイムアウトをおこしたアドレスを保管する
        // なお、設問1からの変化として、タイムアウトを起こした瞬間の時間ではなく
        // タイムアウトを起こしている間のログを集計するためのmap
        Map<AddressPrefix, ErrorInfo> timeOutAddressMap = new HashMap<>();
        // 各アドレスに対してpingの時間を測っておくためのマップ
        Map<AddressPrefix, PingTimeInfo> pingTimeInfoMap = new HashMap<>();
        // pingの時間がtを超えている間のログを集計するためのmap
        Map<AddressPrefix, ErrorInfo> pingOutMeanMap = new HashMap<>();

        LogItem logItem;

        while((logItem=sampleLog.next())!=null){
            if(logItem.isBrokenLog()) continue;
            AddressPrefix address = logItem.getAddressPrefix();

            // タイムアウトに関する処理
            // 設問2と同じ
            {
                boolean isTimeOut = logItem.isTimeOut(); // TimeOutしたかどうか
                boolean beforeTimeOut = timeOutAddressMap.containsKey(address); // TimeOutしているかどうか
                // 以前からタイムアウトしておらず現在もタイムアウトしていないとき(理想)
                if(!beforeTimeOut && !isTimeOut){} // 特に何もせず
                // 以前タイムアウトしていて今回タイムアウトを回避した時
                // この時はタイムアウトした回数と時間を数えて、それを表示する
                // ついでにMapを更新する
                else if(beforeTimeOut && !isTimeOut){
                    // Map の更新
                    ErrorInfo ei = timeOutAddressMap.get(address);
                    timeOutAddressMap.remove(address);
                    //
                    int missCount = ei.getMissCount();
                    if(missCount>=N){
                        LocalDateTime timeOutFrom = ei.firstMissTime;
                        LocalDateTime timeNew = logItem.getDate();
                        long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                        System.out.printf((MESSAGE_HUKKYU02) + "%n", address.toString(), elapsedTime+" 秒", missCount);
                    }else{
                        // System.out.println(String.format(MESSAGE_NOBERAK, address.toString(), missCount));
                    }
                }
                // 以前はタイムアウトしておらず今回タイムアウトした時 (仕方ない)
                // この時は初めてのタイムアウトとしてMapに記録する
                else if(!beforeTimeOut && isTimeOut){
                    timeOutAddressMap.put(address, new ErrorInfo(logItem));
                }
                // 以前からタイムアウトしていて現在もタイムアウト時（重症）
                // この際にはこのアドレスのタイムアウト履歴を更新する,
                else if(beforeTimeOut && isTimeOut){
                    timeOutAddressMap.get(address).add(logItem);
                }}

            // ping に関する処理
            if(!logItem.isTimeOut()) {
                // ping時間
                int newPingTime = logItem.getResponseTime();
                // ping info (これで平均時間を測る)
                PingTimeInfo pingInfo = pingTimeInfoMap.getOrDefault(address, new PingTimeInfo(m));
                // ping 平均
                int newPingTimeMean = pingInfo.newPing(newPingTime);
                // ping 平均が許容時間を超えているか
                boolean isPingOut = newPingTimeMean > t;
                // ping 平均が以前にping時間を超えていたか.
                boolean beforePingOut = pingOutMeanMap.containsKey(address);
                // 以前からping 平均がタイムアウトしておらず現在もタイムアウトしていないとき(理想)
                if(!beforePingOut && !isPingOut){
                }
                // 以前ping 平均がタイムアウトしていて今回タイムアウトを回避した時
                // この時はpingOutMeanMapを編集するのとどれくらいの間超えていたかを出力する
                else if(beforePingOut && !isPingOut){
                    ErrorInfo ei = pingOutMeanMap.get(address);
                    pingOutMeanMap.remove(address);
                    int missCount = ei.getMissCount();
                    LocalDateTime timeOutFrom = ei.firstMissTime;
                    LocalDateTime timeNew = logItem.getDate();
                    long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                    System.out.printf((MESSAGE_KAHUKA_HUKKYU03) + "%n", address.toString(), elapsedTime+" 秒",  missCount, newPingTimeMean);
                }
                // 以前はping 平均がタイムアウトしておらず今回タイムアウトした時
                else if(!beforePingOut && isPingOut){
                    pingOutMeanMap.put(address, new ErrorInfo(logItem));
                    System.out.printf((MESSAGE_KAHUKA03) + "%n", address.toString());
                }
                // 以前からタイムアウトしていて現在もタイムアウト時（重症）
                else if(beforePingOut && isPingOut){
                    pingOutMeanMap.get(address).add(logItem);
                }
            }
        }
        System.out.println("end.");
    }
    public static void task04(String fileName, int N, int m, int t, int N2){
        // 変数名がややこしいので変える
        // 今回はファイルからログを読み取るのでLogByFileクラスを呼び出す.
        Log sampleLog = new LogByFile(fileName);
        // タイムアウトをおこしたアドレスを保管する
        // なお、設問1からの変化として、タイムアウトを起こした瞬間の時間ではなく
        // タイムアウトを起こしている間のログを集計するためのmap
        Map<AddressPrefix, ErrorInfo> timeOutAddressMap = new HashMap<>();
        // タイムアウトを起こしている間のログを集計するためのmap
        // サブネット用
        Map<Integer, ErrorInfo> timeOutSubnetMap = new HashMap<>();
        // 各アドレスに対してpingの時間を測っておくためのマップ
        Map<AddressPrefix, PingTimeInfo> pingTimeInfoMap = new HashMap<>();
        // pingの時間がtを超えている間のログを集計するためのmap
        Map<AddressPrefix, ErrorInfo> pingOutMeanMap = new HashMap<>();

        LogItem logItem;

        while((logItem=sampleLog.next())!=null){
            if(logItem.isBrokenLog()) continue;
            AddressPrefix address = logItem.getAddressPrefix();

            // タイムアウトに関する処理
            // 設問2と同じ
            {
                boolean isTimeOut = logItem.isTimeOut(); // TimeOutしたかどうか
                boolean beforeTimeOut = timeOutAddressMap.containsKey(address); // TimeOutしているかどうか
                // 以前からタイムアウトしておらず現在もタイムアウトしていないとき(理想)
                if(!beforeTimeOut && !isTimeOut){} // 特に何もせず
                // 以前タイムアウトしていて今回タイムアウトを回避した時
                // この時はタイムアウトした回数と時間を数えて、それを表示する
                // ついでにMapを更新する
                else if(beforeTimeOut && !isTimeOut){
                    // Map の更新
                    ErrorInfo ei = timeOutAddressMap.get(address);
                    timeOutAddressMap.remove(address);
                    //
                    int missCount = ei.getMissCount();
                    if(missCount>=N){
                        LocalDateTime timeOutFrom = ei.firstMissTime;
                        LocalDateTime timeNew = logItem.getDate();
                        long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                        System.out.printf((MESSAGE_HUKKYU02) + "%n", address.toString(), elapsedTime+" 秒", missCount);
                    }else{
                        // System.out.println(String.format(MESSAGE_NOBERAK, address.toString(), missCount));
                    }
                }
                // 以前はタイムアウトしておらず今回タイムアウトした時 (仕方ない)
                // この時は初めてのタイムアウトとしてMapに記録する
                else if(!beforeTimeOut && isTimeOut){
                    timeOutAddressMap.put(address, new ErrorInfo(logItem));
                }
                // 以前からタイムアウトしていて現在もタイムアウト時（重症）
                // この際にはこのアドレスのタイムアウト履歴を更新する,
                else if(beforeTimeOut && isTimeOut){
                    timeOutAddressMap.get(address).add(logItem);
                }}

            // タイムアウトに関する処理
            // サブネット用
            // 処理としてはアドレスとほとんど同じ
            // アドレスの代わりにサブネットを用いて, Nの代わりにN2を用いているだけ
            {
                int subnetAddress = address.getSubnet();
                boolean isTimeOut = logItem.isTimeOut(); // TimeOutしたかどうか
                boolean beforeTimeOut = timeOutSubnetMap.containsKey(subnetAddress); // TimeOutしているかどうか
                // 以前からタイムアウトしておらず現在もタイムアウトしていないとき(理想)
                if(!beforeTimeOut && !isTimeOut){} // 特に何もせず
                // 以前タイムアウトしていて今回タイムアウトを回避した時
                // この時はタイムアウトした回数と時間を数えて、それを表示する
                // ついでにMapを更新する
                else if(beforeTimeOut && !isTimeOut){
                    // Map の更新
                    ErrorInfo ei = timeOutSubnetMap.get(subnetAddress);
                    timeOutSubnetMap.remove(subnetAddress);
                    //
                    int missCount = ei.getMissCount();
                    if(missCount>=N2){
                        LocalDateTime timeOutFrom = ei.firstMissTime;
                        LocalDateTime timeNew = logItem.getDate();
                        long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                        System.out.printf((MESSAGE_HUKKYU04) + "%n", AddressPrefix.subnet2Address(subnetAddress), elapsedTime+" 秒", missCount);
                    }else{
                        // System.out.println(String.format(MESSAGE_NOBERAK, AddressPrefix.subnet2Address(subnetAddress), missCount));
                    }
                }
                // 以前はタイムアウトしておらず今回タイムアウトした時 (仕方ない)
                // この時は初めてのタイムアウトとしてMapに記録する
                else if(!beforeTimeOut && isTimeOut){
                    timeOutSubnetMap.put(subnetAddress, new ErrorInfo(logItem));
                }
                // 以前からタイムアウトしていて現在もタイムアウト時（重症）
                // この際にはこのアドレスのタイムアウト履歴を更新する,
                else if(beforeTimeOut && isTimeOut){
                    timeOutSubnetMap.get(subnetAddress).add(logItem);
                }}

            // ping に関する処理
            if(!logItem.isTimeOut()) {
                // ping時間
                int newPingTime = logItem.getResponseTime();
                // ping info (これで平均時間を測る)
                PingTimeInfo pingInfo = pingTimeInfoMap.getOrDefault(address, new PingTimeInfo(m));
                // ping 平均
                int newPingTimeMean = pingInfo.newPing(newPingTime);
                // ping 平均が許容時間を超えているか
                boolean isPingOut = newPingTimeMean > t;
                // ping 平均が以前にping時間を超えていたか.
                boolean beforePingOut = pingOutMeanMap.containsKey(address);
                // 以前からping 平均がタイムアウトしておらず現在もタイムアウトしていないとき(理想)
                if(!beforePingOut && !isPingOut){
                }
                // 以前ping 平均がタイムアウトしていて今回タイムアウトを回避した時
                // この時はpingOutMeanMapを編集するのとどれくらいの間超えていたかを出力する
                else if(beforePingOut && !isPingOut){
                    ErrorInfo ei = pingOutMeanMap.get(address);
                    pingOutMeanMap.remove(address);
                    int missCount = ei.getMissCount();
                    LocalDateTime timeOutFrom = ei.firstMissTime;
                    LocalDateTime timeNew = logItem.getDate();
                    long elapsedTime = getElapsedTime(timeOutFrom, timeNew);
                    System.out.printf((MESSAGE_KAHUKA_HUKKYU03) + "%n", address.toString(), elapsedTime+" 秒",  missCount, newPingTimeMean);
                }
                // 以前はping 平均がタイムアウトしておらず今回タイムアウトした時
                else if(!beforePingOut && isPingOut){
                    pingOutMeanMap.put(address, new ErrorInfo(logItem));
                }
                // 以前からタイムアウトしていて現在もタイムアウト時（重症）
                else if(beforePingOut && isPingOut){
                    pingOutMeanMap.get(address).add(logItem);
                }
            }
        }
        System.out.println("end.");
    }

    public static void main(String[] args) {
        // task00();
        task01("/testcase20.txt");
    }
}