package com.codetest;

/**
 * @Author 金田燎弥
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void task01() {
        System.out.println("task01()");
        Main.task01("/testcase20.txt");
        Main.task01("/testcase20.txt");
    }

    @Test
    void task02() {
        System.out.println("task02()");
        Main.task02("/testcase20.txt", 2);
        Main.task02("/testcase20.txt", 4);
    }

    @Test
    void task03() {
        System.out.println("task03()");
        Main.task03("/testcase20.txt", 2, 1, 5);
        Main.task03("/testcase20.txt", 2, 3, 5);
    }

    @Test
    void task04() {
        System.out.println("task04()");
        Main.task04("/testcase20.txt", 2, 1, 5, 2);
        Main.task04("/testcase20.txt", 2, 1, 5, 5);

    }
}