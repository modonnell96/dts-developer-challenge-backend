package uk.gov.hmcts.reform.dev.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateTaskStatusRequest {
    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

}
