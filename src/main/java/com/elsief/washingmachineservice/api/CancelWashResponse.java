package com.elsief.washingmachineservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelWashResponse {
    @JsonProperty("washId")
    private Long washId;
    @JsonProperty("status")
    private HttpStatus httpStatus;
    @JsonProperty("message")
    private String message;
}
