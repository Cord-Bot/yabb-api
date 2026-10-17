package io.github.eggy03.yabb.configuration;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@ApplicationScoped
public class FixedClockProducer {

    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    public Clock clock() {
        return Clock.fixed(
                Instant.parse("2002-06-07T12:00:00Z"),
                ZoneOffset.UTC
        );
    }
}
