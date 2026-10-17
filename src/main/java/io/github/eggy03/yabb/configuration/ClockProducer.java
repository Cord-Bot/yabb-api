package io.github.eggy03.yabb.configuration;

import io.github.eggy03.yabb.annotation.UTCClock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;

@ApplicationScoped
public class ClockProducer {

    @Produces
    @ApplicationScoped
    @UTCClock
    public Clock clock() {
        return Clock.systemUTC();
    }
}
