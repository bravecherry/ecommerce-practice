package fastcampus.ecommerce.batch.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 모르는 필드가 나오면 무시
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMdc {

    private String transactionType;
    private String totalAmount;
    private String orderId;
    private String transactionStatus;
    private String customerId;
    private String paymentMethod;
    private String productCount;
    private String totalItemQuantity;

    public String getTotalAmount() {
        if (totalAmount.equals("N/A")) {
            return "0";
        }
        return totalAmount;
    }

}
