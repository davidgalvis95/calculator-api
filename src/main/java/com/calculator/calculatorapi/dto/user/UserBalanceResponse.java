package com.calculator.calculatorapi.dto.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserBalanceResponse {
    Integer newBalance;
}
