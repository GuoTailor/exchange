package com.exchange;

import com.exchange.enums.KLinePeriod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;

public class BaseTest {
    String s = "1";

    @Test
    public void testReactor() throws InterruptedException {
        Mono.just("1111")
                .doOnNext(System.out::println)
                .map(it -> {
                    System.out.println(s);
                    s = s + "1";
                    return it;
                })
                .filter(it -> s.equals(it))
                .switchIfEmpty(Mono.error(new RuntimeException("boom")))
                .retry(1)
                .subscribe(System.out::println, System.err::println);

        Thread.sleep(2100);
        System.out.println(">>>" + s);
    }

    @Test
    public void testRecord() throws JsonProcessingException {
        var user = new User("nmka", 12);
        var json = new ObjectMapper();
        String s = json.writeValueAsString(user);
        System.out.println(s);
        User user1 = json.readValue(s, User.class);
        System.out.println(user1);
    }

    @Test
    public void testTime() throws JsonProcessingException {
        Duration duration = Duration.ofNanos(LocalTime.MAX.toNanoOfDay());
        System.out.println(duration.toNanos());
        System.out.println(duration.toSecondsPart());
        LocalTime localTime = LocalTime.ofSecondOfDay(72);
        System.out.println(localTime.toNanoOfDay());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println(objectMapper.writeValueAsString(duration));
    }

    @Test
    public void testLocke() throws InterruptedException {
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (KLinePeriod.LD) {
                System.out.println("2 >>>> start");
                KLinePeriod.LD.notifyAll();
                System.out.println("2 >>>> sleep");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("2 >>>> end");
            }
        });

        Thread thread = new Thread(() -> {
            synchronized (KLinePeriod.LD) {
                System.out.println("1 >>>> start");
                try {
                    t2.start();
                    KLinePeriod.LD.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("1 >>>> end");
            }
        });
        thread.start();

        Thread thread1 = new Thread(() -> {
            synchronized (KLinePeriod.LD) {
                System.out.println("3 >>>> start");
                try {
                    KLinePeriod.LD.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("3 >>>> end");
            }
        });
        thread1.start();
        thread.join();
        thread1.join();
    }
}
