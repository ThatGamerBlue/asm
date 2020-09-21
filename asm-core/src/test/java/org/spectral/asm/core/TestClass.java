package org.spectral.asm.core;

import java.util.ArrayList;
import java.util.List;

public class TestClass {

    private final List<Message> messages = new ArrayList<>();

    public static void run() {
        System.out.println("Running Test Class");

        TestClass testClass = new TestClass();
        testClass.registerMessages();
        testClass.printMessages();
    }

    public void printMessages() {
        for(Message msg : messages) {
            System.out.println("Name: " + msg.getName() + " - " + msg.getMessage());
        }
    }

    private void registerMessages() {
        this.messages.add(new Message("test1", "hello from test1"));
    }
}
