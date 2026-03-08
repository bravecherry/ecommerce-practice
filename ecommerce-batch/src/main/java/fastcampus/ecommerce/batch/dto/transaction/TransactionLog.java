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
public class TransactionLog {

    private String timestamp;
    private String level;
    private String thread;
    private String logger;
    private String message;
    private TransactionMdc mdc;

    public String getTransactionType() {
        return mdc == null ? null : mdc.getTransactionType();
    }

    public String getTotalAmount() {
        return mdc == null ? null : mdc.getTotalAmount();
    }

    public String getOrderId() {
        return mdc == null ? null : mdc.getOrderId();
    }

    public String getTransactionStatus() {
        return mdc == null ? null : mdc.getTransactionStatus();
    }

    public String getCustomerId() {
        return mdc == null ? null : mdc.getCustomerId();
    }

    public String getPaymentMethod() {
        return mdc == null ? null : mdc.getPaymentMethod();
    }

    public String getProductCount() {
        return mdc == null ? null : mdc.getProductCount();
    }

    public String getTotalItemQuantity() {
        return mdc == null ? null : mdc.getTotalItemQuantity();
    }

}
