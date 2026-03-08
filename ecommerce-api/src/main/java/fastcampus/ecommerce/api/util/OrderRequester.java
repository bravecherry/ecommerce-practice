package fastcampus.ecommerce.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.ecommerce.api.controller.order.OrderItemRequest;
import fastcampus.ecommerce.api.controller.order.OrderRequest;
import fastcampus.ecommerce.api.controller.order.OrderResponse;
import fastcampus.ecommerce.api.controller.order.PaymentRequest;
import fastcampus.ecommerce.api.domain.payment.PaymentMethod;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderRequester {

    private static final String BASE_URL = "http://localhost:8080/v1";
    private static final String PRODUCTS_URL = BASE_URL + "/products";
    private static final String ORDERS_URL = BASE_URL + "/orders";
    private static final Random RANDOM = new Random();
    private static final PaymentMethod[] PAYMENT_METHODS = PaymentMethod.values();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        int maxWorker = 20;
        ExecutorService executor = Executors.newFixedThreadPool(maxWorker);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try {
            int page = 0;
            int size = 1000;
            boolean hasNextPage = true;
            while (hasNextPage && page < 10000) {
                String productJson = fetchProduct(page, size);
                JsonNode productsNode = OBJECT_MAPPER.readTree(productJson);
                JsonNode contentNode = productsNode.get("content");

                for (JsonNode productNode : contentNode) {
                    String productId = productNode.get("id").asText();
                    int stockQuantity = productNode.get("stockQuantity").asInt();
                    CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processProduct(productId, stockQuantity), executor);
                    futures.add(future);
                }
                hasNextPage = !productsNode.get("last").asBoolean();
                page++;
            }
        } catch (RuntimeException | IOException | InterruptedException e) {
            log.error(e.getMessage());
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }

    private static void processProduct(String productId, int stockQuantity) {
        int quantity = Math.max((int) Math.floor(stockQuantity / 10.0), 1);
        int randomNum = RANDOM.nextInt(16);

        OrderResponse res = createOrder(productId, quantity);
        if (res != null) {
            if (randomNum % 4 < 2) {
                completePayment(res.getOrderId(), randomNum % 2 == 0);
            }
            if (randomNum % 8 < 4) {
                completeOrder(res.getOrderId());
            }
            // 16개로 나눈 이유는 경우의 수가 16가지가 될 수 있기에
            if (randomNum % 16 < 8) {
                cancelOrder(res.getOrderId());
            }
        }

    }

    private static void cancelOrder(Long orderId) {
        try {
            HttpResponse<String> httpResponse = sendPostRequest(
                ORDERS_URL + "/" + orderId + "/cancel", "");
            if (httpResponse.statusCode() == 200) {
                log.info("주문 취소 처리 완료");
            } else {
                log.info("주문 취소 중 API 응답 실패");
            }
        } catch (Exception e) {
            log.error("주문 취소 중 에러 발생 >> {}", e.getMessage());
        }
    }

    private static void completeOrder(Long orderId) {
        try {
            HttpResponse<String> httpResponse = sendPostRequest(
                ORDERS_URL + "/" + orderId + "/complete", "");
            if (httpResponse.statusCode() == 200) {
                log.info("결제 처리 완료");
            } else {
                log.info("결제 처리 중 API 응답 실패");
            }
        } catch (Exception e) {
            log.error("결제 중 에러 발생 >> {}", e.getMessage());
        }
    }

    private static void completePayment(Long orderId, boolean success) {
        PaymentRequest request = PaymentRequest.of(success);
        try {
            String requestBody = OBJECT_MAPPER.writeValueAsString(request);
            HttpResponse<String> httpResponse = sendPostRequest(
                ORDERS_URL + "/" + orderId + "/payment", requestBody);
            if (httpResponse.statusCode() == 200) {
                log.info(success ? "결제 처리 완료" : "결제 처리 신호");
            } else {
                log.info(success ? "결제 처리 완료" : "결제 처리 신호");
            }
        } catch (Exception e) {
            log.error("주문 생성 중 에러 발생 >> {}", e.getMessage());
        }
    }

    private static OrderResponse createOrder(String productId, int quantity) {
        OrderRequest request = OrderRequest.of((long) randomCustomerId(),
            List.of(OrderItemRequest.of(productId, quantity)),
            PAYMENT_METHODS[RANDOM.nextInt(PaymentMethod.values().length)]);
        try {
            String requestBody = OBJECT_MAPPER.writeValueAsString(request);
            HttpResponse<String> httpResponse = sendPostRequest(ORDERS_URL, requestBody);
            if (httpResponse.statusCode() == 200) {
                return OBJECT_MAPPER.readValue(httpResponse.body(), OrderResponse.class);
            } else {
                log.error("주문 성공");
                return null;
            }
        } catch (Exception e) {
            log.error("주문 생성 중 에러 발생 >> {}", e.getMessage());
            return null;
        }
    }

    private static HttpResponse<String> sendPostRequest(String url, String requestBody)
        throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    private static int randomCustomerId() {
        return RANDOM.nextInt(1000) + 1;
    }

    private static String fetchProduct(int page, int size)
        throws IOException, InterruptedException {
        String url = String.format("%s?page=%d&size=%d&order=productId,asc",
            PRODUCTS_URL, page, size);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .GET()
            .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request,
            HttpResponse.BodyHandlers.ofString());
        return response.body();

    }
}