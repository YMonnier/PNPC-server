package fr.pnpc.project.server;
import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;

import java.util.concurrent.CountDownLatch;

import static java.lang.Integer.parseInt;

public class HerokuRunner {
    public static void main(final String[] args) throws InterruptedException {
        try (Container container = new Container(
                new Configuration().http(parseInt(System.getenv("PORT"))))
                .deployClasspathAsWebApp("/", null)) {
            new CountDownLatch(1).await();
        }
    }
}
