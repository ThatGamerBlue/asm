package org.spectral.asm.core;

import java.util.ArrayList;
import java.util.List;

public class TestClass {

    private final List<Message> messages = new ArrayList<>();

    private Integer size = 0;

    public static void printEnd() {
        System.out.println("Done printing messages.");
    }

    public static void run() {
        System.out.println("Running Test Class");

        TestClass testClass = new TestClass();
        testClass.registerMessages();
        testClass.printMessages();

        TestClass.printEnd();
    }

    public void printMessages() {
        for(Message msg : messages) {
            System.out.println("Name: " + msg.getName() + " - " + msg.getMessage());
        }
    }

    private void registerMessages() {
        this.messages.add(new Message("test1", "hello from test1"));
        size = this.messages.size();
    }
}
