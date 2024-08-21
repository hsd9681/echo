package com.echo.echo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class TempTest {

    @Test
    void tes1() throws InterruptedException {
        Mono<Void> mono1 = Mono.just("hello world")
                .doOnNext(data -> System.out.println("doOnNext: " + data))
                .then();

        mono1.subscribe();
        mono1.subscribe();

        Thread.sleep(1000);
    }

    @Test
    void test2() throws InterruptedException {
//        Mono<Map<Integer, String>> numLabelMapMono =
//                Flux.just(Tuples.of(1, "일"), Tuples.of(2, "이"), Tuples.of(3, "삼"), Tuples.of(4, "사"))
//                        .collectMap(x -> x.getT1(),
//                                x -> x.getT2());

        List<TestObject> testObj =  List.of(new TestObject("id1", "name1"), new TestObject("id2", "name2"));

        AtomicInteger i = new AtomicInteger();
        Flux.just(testObj)
                .collectMap(x -> i.incrementAndGet())
                .subscribe(data -> System.out.println(data));

        Thread.sleep(1000);
    }

    public static class TestObject {
        private String id;
        private String name;

        TestObject(String id, String name){
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
