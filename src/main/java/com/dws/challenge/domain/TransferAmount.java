package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Getter
@Setter
public class TransferAmount {

    @NotNull
    @NotEmpty
    private final String accountFromId;

    @NotNull
    @NotEmpty
    private final String accountToId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal amount;

    @JsonCreator
    public TransferAmount(@JsonProperty("accountFromId") String accountFromId,
                   @JsonProperty("accountToId") String accountToId,
                   @JsonProperty("amount") BigDecimal amount) {
        this.accountToId = accountToId;
        this.accountFromId = accountFromId;
        this.amount = amount;
    }
}
