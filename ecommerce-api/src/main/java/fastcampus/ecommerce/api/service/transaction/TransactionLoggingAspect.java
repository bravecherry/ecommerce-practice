package fastcampus.ecommerce.api.service.transaction;

import fastcampus.ecommerce.api.domain.order.OrderResult;
import fastcampus.ecommerce.api.domain.transaction.TransactionStatus;
import fastcampus.ecommerce.api.domain.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionLoggingAspect {

    private final TransactionService transactionService;

    @Pointcut("execution(* fastcampus.ecommerce.api.service.order.OrderService.order(..))")
    public void orderCreation() {

    }

    @AfterReturning(pointcut = "orderCreation()", returning = "newOrder")
    public void afterOrderCreationSuccess(Object newOrder) {
        transactionService.logTransaction(TransactionType.ORDER_CREATION, TransactionStatus.SUCCESS,
            "주문을 성공했습니다.", (OrderResult) newOrder);
    }

    @AfterThrowing(pointcut = "orderCreation()", throwing = "exception")
    public void afterOrderCreationFailure(Exception exception) {
        transactionService.logTransaction(TransactionType.ORDER_CREATION, TransactionStatus.FAILURE,
            "주문 처리 중 오류가 발생하였습니다. 상세: " + exception.getMessage(), null);
    }

    @Pointcut("execution(* fastcampus.ecommerce.api.service.order.OrderService.completePayment(..))")
    public void paymentComplete() {

    }

    @AfterReturning(pointcut = "paymentComplete()", returning = "updatedOrder")
    public void afterPaymentCompletionSuccess(Object updatedOrder) {
        if (((OrderResult) updatedOrder).isPaymentSuccess()) {
            transactionService.logTransaction(TransactionType.PAYMENT_COMPLETION,
                TransactionStatus.SUCCESS, "결제를 성공했습니다.", (OrderResult) updatedOrder);
        } else {
            transactionService.logTransaction(TransactionType.PAYMENT_COMPLETION,
                TransactionStatus.FAILURE, "결제를 실패했습니다.", (OrderResult) updatedOrder);
        }
    }

    @AfterThrowing(pointcut = "paymentComplete()", throwing = "exception")
    public void afterPaymentCompletionFailure(Exception exception) {
        transactionService.logTransaction(TransactionType.PAYMENT_COMPLETION,
            TransactionStatus.FAILURE, "결제 처리 중 오류가 발생하였습니다. 상세: " + exception.getMessage(), null);
    }

    @Pointcut("execution(* fastcampus.ecommerce.api.service.order.OrderService.cancelOrder(..))")
    public void orderCancellation() {

    }

    @AfterReturning(pointcut = "orderCancellation()", returning = "cancelledOrder")
    public void afterorderCancellationSuccess(Object cancelledOrder) {
        transactionService.logTransaction(TransactionType.ORDER_CANCELLATION,
            TransactionStatus.SUCCESS, "주문이 취소되었습니다.", (OrderResult) cancelledOrder);
    }

    @AfterThrowing(pointcut = "orderCancellation()", throwing = "exception")
    public void afterorderCancellationFailure(Exception exception) {
        transactionService.logTransaction(TransactionType.ORDER_CANCELLATION,
            TransactionStatus.FAILURE, "주문 실패 처리 중 오류가 발생하였습니다. 상세: " + exception.getMessage(),
            null);
    }

}
