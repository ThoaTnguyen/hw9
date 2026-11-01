package patterns;

import patterns.flyweight.FlyweightDriver;
import patterns.proxy.ProxyDriver;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("Flyweight: Text Editor");
        FlyweightDriver.run().forEach(System.out::println);
        System.out.println();
        System.out.println("Proxy: Music Streaming");
        ProxyDriver.run().forEach(System.out::println);
    }
}
