package com.elsief.washingmachineservice.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWashRequest {
    @JsonProperty("appliance")
    private long applianceId;
    @JsonProperty("program")
    private long programId;
    @JsonProperty
    private LocalDateTime startTime;
}
