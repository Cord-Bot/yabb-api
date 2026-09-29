package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.service.BirthdayNotificationService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Path("api/v1/birthday/notification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@ApplicationScoped
public class BirthdayNotificationController {

    private final @NonNull BirthdayNotificationService birthdayNotificationService;

    @GET
    @Path("/{guildId}/{userId}")
    @Blocking
    @RunOnVirtualThread
    public Response getNotification(@PathParam("guildId") @Positive long guildId, @PathParam("userId") @Positive long userId) {
        return Response
                .ok(birthdayNotificationService.getNotification(guildId, userId))
                .build();
    }

    @GET
    @Blocking
    @RunOnVirtualThread
    public Response getPendingNotifications() {
        return Response
                .ok(birthdayNotificationService.getPendingNotifications())
                .build();
    }

    @PUT
    @Path("/{guildId}/{userId}/{zoneId}")
    @Blocking
    @RunOnVirtualThread
    public Response updateNotificationForNextYear(@PathParam("guildId") @Positive long guildId, @PathParam("userId") @Positive long userId, @PathParam("zoneId") @NotNull String zoneId) {
        birthdayNotificationService.updateNotificationForNextYear(guildId, userId, zoneId);
        return Response.noContent().build();
    }
}
