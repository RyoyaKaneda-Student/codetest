package com.codetest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author 金田燎弥
 */

class LogItemTest {

    @Test
    void isTimeOut() {
        LogItem li1 = new LogItem(
                "20201019133124",
                "10.20.30.1/16",
                "-"
        );
        LogItem li2 = new LogItem(
                "20201019133124",
                "10.20.30.1/16",
                "1"
        );
        Assertions.assertEquals(true, li1.isTimeOut());
        Assertions.assertEquals(false, li2.isTimeOut() );
    }

    @Test
    void isBrokenLog() {
        LogItem li1 = new LogItem(
                "notTrueDate",
                "10.20.30.1/16",
                "-"
        );
        LogItem li2 = new LogItem(
                "20201019133124",
                "10",
                "1"
        );
        LogItem li3 = new LogItem(
                "20201019133124",
                "10.20.30.1/16",
                "1"
        );
        Assertions.assertEquals(true, li1.isBrokenLog());
        Assertions.assertEquals(true, li2.isBrokenLog());
        Assertions.assertEquals(false, li3.isBrokenLog());
    }
}