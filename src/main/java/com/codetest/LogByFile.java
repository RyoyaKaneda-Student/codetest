package com.codetest;

/**
 * @Author 金田燎弥
 */

import java.io.*;
import java.util.ArrayList;

public class LogByFile implements Log {
    private ArrayList<LogItem> logList;
    private int index;

    // コンストラクタ
    public LogByFile(String fileName) {
        ArrayList<LogItem> logList = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String text;
            while ((text = br.readLine()) != null) {
                // System.out.println(text);
                String tmp[] = text.split(",");
                LogItem newItem = new LogItem(tmp[0], tmp[1], tmp[2]);
                logList.add(newItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.logList = logList;
    }

    @Override
    public LogItem next() {
        if (this.index == this.logList.size()) {
            return null;
        } else {
            return this.logList.get(this.index++);
        }
    }

}
