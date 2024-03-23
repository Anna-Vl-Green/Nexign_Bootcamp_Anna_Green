package ru.test.nexigntest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Udr {
    private String msisdn;
    private UdrTime incomingCall;
    private UdrTime outcomingCall;
}
