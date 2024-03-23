package ru.test.nexigntest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cdr {
    private Byte callType;
    private Long msisdn;
    private Long startTime;
    private Long endTime;
}