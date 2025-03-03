package net.javaguides.springboot.banking.dto;


/*import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor*/
public record AccountDto (Long id,
						String accountHolderName,
						double balance) {
}

