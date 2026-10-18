package io.github.eggy03.yabb.exception.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Data
@AllArgsConstructor
@RegisterForReflection
@Schema(name = "ErrorResponse", description = "Standard API error response")
public class ErrorResponse {

    @Schema(required = true, readOnly = true)
    private int status;

    @Schema(required = true, readOnly = true)
    private String error;

    @Schema(required = true, readOnly = true)
    private String message;

    @Schema(required = true, readOnly = true, type = SchemaType.STRING, format = "date-time", examples = "2026-10-13T00:00:00Z")
    private Instant timeStamp;

    @Schema(required = true, readOnly = true)
    private String path;
}
