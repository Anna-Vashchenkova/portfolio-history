package ru.gubber.portfoliohistory.operation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gubber.portfoliohistory.account.controller.AccountController;
import ru.gubber.portfoliohistory.account.dto.*;
import ru.gubber.portfoliohistory.common.utils.FieldValidationError;
import ru.gubber.portfoliohistory.common.utils.ValidationUtils;
import ru.gubber.portfoliohistory.operation.dto.OperationDto;
import ru.gubber.portfoliohistory.operation.dto.OutcomeOperationDto;
import ru.gubber.portfoliohistory.operation.dto.ResultOperationId;
import ru.gubber.portfoliohistory.operation.service.OperationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class OperationController {
    private final OperationService operationService;
    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/api/v1/replenish-account")
    public BaseResponse replenishAccount(@RequestBody OperationDto dto) {
        log.info("Получен запрос на пополнение счета {}.", dto.accountId());
        List<FieldValidationError> invalidField = new ArrayList<>();
        invalidField.add(ValidationUtils.validateStringField(dto.accountId()));
        invalidField.add(ValidationUtils.validateNumberField(dto.amount(), "amount"));
        List<FieldValidationError> validationErrors = invalidField.stream().filter(Objects::nonNull).toList();
        if (!validationErrors.isEmpty()) {
            log.info("Ошибка валидации - не правильный запрос.");
            return new ValidationError(ResponseStatus.ERROR, "Не правильный запрос", validationErrors);
        }
        UUID uuid = operationService.replenishAccount(dto.accountId(), dto.amount());
        if (uuid != null) {
            log.info("Запрос успешно выполнен.");
            return new OutcomeOperationDto(new ResultOperationId(uuid));
        } else {
            log.info("Не удалось выполнить запрос на добавление актива на счет {}", dto.accountId());
            return new ValidationError(ResponseStatus.WARN,
                    String.format("На счет %s не удалось добавить актив", dto.accountId()), null);
        }
    }
}