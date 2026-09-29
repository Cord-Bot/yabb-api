package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.service.BirthdayService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Path("api/v1/birthday")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@ApplicationScoped
public class BirthdayController {

    private final @NonNull BirthdayService birthdayService;

    @POST
    @Blocking
    @RunOnVirtualThread
    public Response saveBirthday(@Valid BirthdayDTO dto) {
        birthdayService.saveBirthday(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{guildId}/{userId}")
    @Blocking
    @RunOnVirtualThread
    public Response getBirthday(@PathParam("guildId") @Positive long guildId, @PathParam("userId") @Positive long userId) {
        return Response
                .ok(birthdayService.getBirthday(guildId, userId))
                .build();
    }

    @GET
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    public Response getBirthdays(@PathParam("guildId") @Positive long guildId) {
        return Response
                .ok(birthdayService.getBirthdays(guildId))
                .build();
    }

    @PUT
    @Blocking
    @RunOnVirtualThread
    public Response updateBirthday(@Valid BirthdayDTO updatedDTO) {
        birthdayService.updateBirthday(updatedDTO);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{guildId}/{userId}")
    @Blocking
    @RunOnVirtualThread
    public Response deleteBirthday(@PathParam("guildId") @Positive long guildId, @PathParam("userId") @Positive long userId) {
        birthdayService.deleteBirthday(guildId, userId);
        return Response.noContent().build();
    }
}
