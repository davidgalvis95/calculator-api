package com.calculator.calculatorapi.dto.operations;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Valid
@AllArgsConstructor
public class NumberOperationValues implements OperationValues {
    private double a;
    @Nullable
    private Double b;

    public NumberOperationValues(int a) {
        this.a = a;
    }
}
