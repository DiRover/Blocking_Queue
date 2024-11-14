package di_rover;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static final AtomicInteger processedCount = new AtomicInteger();
    static int textLength = 100_000;
    static int textAmount = 10_000;
    static String letters = "abc";
    static char a = 'a';
    static char b = 'b';
    static char c = 'c';
    static AtomicInteger countA = new AtomicInteger();
    static AtomicInteger countB = new AtomicInteger();
    static AtomicInteger countC = new AtomicInteger();
    static BlockingQueue<String> queueFirst = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueSecond = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> queueThird = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            for (int i = 0; i < textAmount; i++) {
                String text = generateText();
                boolean isAdded = true;

                while (isAdded) {
                    isAdded = queueFirst.offer(text);
                    if (!isAdded) {
                        isAdded = queueSecond.offer(text);
                    }
                    if (!isAdded) {
                        isAdded = queueThird.offer(text);
                    }
                }
            }
        }).start();

        Thread threadFirst = new Thread(() -> {
            try {
                while (processedCount.get() <= textAmount) {
                    processedCount.incrementAndGet();

                    String text = queueFirst.take();

                    long count = text.chars()
                            .filter(ch -> ch == a)
                            .count();


                    if (count > countA.get()) {
                        countA.getAndSet((int) count);
                    }
                }

            } catch (InterruptedException e) {
                return;
            }
        });

        threadFirst.start();

        Thread threadSecond = new Thread(() -> {
            try {
                while (processedCount.get() <= textAmount) {
                    processedCount.incrementAndGet();

                    String text = queueSecond.take();

                    long count = text.chars()
                            .filter(ch -> ch == b)
                            .count();


                    if (count > countB.get()) {
                        countB.getAndSet((int) count);
                    }
                }

            } catch (InterruptedException e) {
                return;
            }
        });

        threadSecond.start();

        Thread threadThird = new Thread(() -> {
            try {
                while (processedCount.get() <= textAmount) {
                    processedCount.incrementAndGet();

                    String text = queueThird.take();

                    long count = text.chars()
                            .filter(ch -> ch == c)
                            .count();

                    if (count > countC.get()) {
                        countC.getAndSet((int) count);
                    }
                }

            } catch (InterruptedException e) {
                return;
            }
        });

        threadThird.start();

        threadFirst.join();
        threadSecond.join();
        threadThird.join();


        System.out.println("count of A - " + countA.get());
        System.out.println("count of B - " + countB.get());
        System.out.println("count of C - " + countC.get());
    }

    public static String generateText() {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}