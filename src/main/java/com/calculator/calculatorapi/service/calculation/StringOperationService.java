package com.calculator.calculatorapi.service.calculation;

import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.operations.OperationRequest;
import com.calculator.calculatorapi.dto.operations.OperationResponse;
import com.calculator.calculatorapi.dto.operations.StringOperationResult;
import com.calculator.calculatorapi.dto.operations.StringOperationValues;
import com.calculator.calculatorapi.models.Operation;
import com.calculator.calculatorapi.models.OperationState;
import com.calculator.calculatorapi.models.OperationType;
import com.calculator.calculatorapi.models.Record;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.repository.OperationRepository;
import com.calculator.calculatorapi.repository.RecordRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import com.calculator.calculatorapi.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class StringOperationService implements CalculationService<StringOperationValues, String> {

    private final RecordRepository recordRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;

    @Autowired
    public StringOperationService(final RecordRepository recordRepository,
                                  final OperationRepository operationRepository,
                                  final UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.operationRepository = operationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OperationResponse<String> processOperation(OperationRequest<StringOperationValues> operationRequest) {
        if (!operationRequest.getOperationType().equals(OperationType.RANDOM_STRING)) {
            throw new IllegalArgumentException("Invalid operation type, please choose one of the following: " +
                    "[RANDOM_STRING]");
        }
        int size = operationRequest.getOperands().getSize();
        String alphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";

        StringBuilder sb = new StringBuilder(size);
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            int randomIndex = random.nextInt(alphaNumericStr.length());
            char randomChar = alphaNumericStr.charAt(randomIndex);
            sb.append(randomChar);
        }

        final UUID userId = Utils.getLoggedUserId();
        if (userId != null) {
            return processOperationResult(operationRequest, sb, userId);
        } else {
            throw new BadCredentialsException("Unrecognized connected user, aborting transaction");
        }
    }

    @Transactional
    protected OperationResponse<String> processOperationResult(final OperationRequest<StringOperationValues> operationRequest,
                                                             final StringBuilder response,
                                                             final UUID userId) {
        final User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
        final OperationType operationType = operationRequest.getOperationType();
        final Operation operation = operationRepository.findOperationByType(operationType)
                .orElseThrow(() -> new ObjectNotFoundException("Operation of type " + operationType + " not found"));
        final int operationCost = operation.getCost();
        final int userBalance = user.getBalance();
        if (userBalance < operationCost) {
            throw new IllegalStateException("User has less balance than required to perform " + operationType +
                    ", current balance:  " + userBalance + ", current cost: " + operationCost);
        }
        final int newUserBalance = userBalance - operationCost;
        user.setBalance(newUserBalance);
        final LocalDateTime dateTime = LocalDateTime.now();

        recordRepository.save(Record.builder()
                .amount(operationCost)
                .dateTime(dateTime)
                .userBalance(newUserBalance)
                .userId(user)
                .operationId(operation)
                .operationState(OperationState.SUCCEEDED)
                .build());

        return new OperationResponse<>(
                UUID.randomUUID(),
                user.getEmail(),
                operationRequest.getOperationType(),
                OperationState.SUCCEEDED,
                operationRequest.getOperands(),
                operationCost,
                newUserBalance,
                dateTime,
                StringOperationResult.builder().result(response.toString()).build());
    }
}
