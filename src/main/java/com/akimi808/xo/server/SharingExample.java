package com.akimi808.xo.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimi808 on 05/03/2018.
 */
public class SharingExample {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        MyStrangeClass first = new MyStrangeClass(list, "first");
        MyStrangeClass second = new MyStrangeClass(list, "second");
        first.doSomeWork();
        second.doSomeWork();
        System.out.println(list);
    }


    static class MyStrangeClass {
        final List<String> storage;
        private String id;

        MyStrangeClass(List<String> storage, String id) {
            this.storage = storage;
            this.id = id;
        }

        public void doSomeWork() {
            this.storage.add("Data " + id);
        }
    }
}
