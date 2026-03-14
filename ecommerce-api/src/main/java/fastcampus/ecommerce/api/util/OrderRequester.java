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

/**
 * 거래 로그를 만들기 위해 주문 자동 처리 스크립트 생성(천 만 건)
 */
@Slf4j
public class OrderRequester {

    // url 정의
    private static final String BASE_URL = "http://localhost:8080/v1";
    private static final String PRODUCTS_URL = BASE_URL + "/products";
    private static final String ORDERS_URL = BASE_URL + "/orders";
    // 초기화
    private static final Random RANDOM = new Random();
    private static final PaymentMethod[] PAYMENT_METHODS = PaymentMethod.values();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        int maxWorker = 20;
        // A 설명: 고정된 스레드 풀로 실행
        ExecutorService executor = Executors.newFixedThreadPool(maxWorker);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try {
            // 상품을 페이징 해와서 조회
            // 천 만개 호출 하기 위해 size = 1000, page = 10000 설정
            int page = 0;
            int size = 1000;
            boolean hasNextPage = true;
            while (hasNextPage && page < 10000) {
                String productJson = fetchProduct(page, size);
                //DTO 매핑하는 대신 JSON node 로 받아와서 필드 조회하는 방식으로(간단하게) 작업
                JsonNode productsNode = OBJECT_MAPPER.readTree(productJson);
                JsonNode contentNode = productsNode.get("content");

                for (JsonNode productNode : contentNode) {
                    // 필드 조회
                    String productId = productNode.get("productId").asText();
                    int stockQuantity = productNode.get("stockQuantity").asInt();
                    // 주문 요청 시도(future)
                    CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processProduct(productId, stockQuantity), executor);
                    // executor 에 future 저장
                    futures.add(future);
                }
                // 다음 페이지 여부 확인
                hasNextPage = !productsNode.get("last").asBoolean();
                page++;
            }
        } catch (RuntimeException | IOException | InterruptedException e) {
            log.error(e.getMessage());
        }

        // A`설명: 각 스레드가 종료가 되었을 때 join 통해서 전체 스레드가 종료되면 executor 셧다운 시키고 프로그램도 종료 처리
        //모든 future 들에 대해 join을 하고,
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        //executor 셧다운 처리
        executor.shutdown();
    }

    /**
     * 주문 처리
     */
    private static void processProduct(String productId, int stockQuantity) {
        //주문량은 상품 수의 1/10 규모로 설정
        int quantity = Math.max((int) Math.floor(stockQuantity / 10.0), 1);
        //여러 케이스로 나뉘는 주문 처리 데이터를 생성하기 위해 random number
        int randomNum = RANDOM.nextInt(16);

        //주문 진행
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
                log.info("완료");
            } else {
                log.error("API 응답 실패");
            }
        } catch (Exception e) {
            log.error("예외 발생 >> {}", e.getMessage());
        }
    }

    private static void completeOrder(Long orderId) {
        try {
            HttpResponse<String> httpResponse = sendPostRequest(
                ORDERS_URL + "/" + orderId + "/complete", "");
            if (httpResponse.statusCode() == 200) {
                log.info("완료");
            } else {
                log.error("API 응답 실패");
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
                log.info(success ? "완료" : "실패");
            } else {
                log.error("API 응답 실패");
            }
        } catch (Exception e) {
            log.error("예외 발생 >> {}", e.getMessage());
        }
    }

    private static OrderResponse createOrder(String productId, int quantity) {
        OrderRequest request = OrderRequest.of((long) randomCustomerId(),
            List.of(OrderItemRequest.of(productId, quantity)),
            PAYMENT_METHODS[RANDOM.nextInt(PaymentMethod.values().length)]);
        try {
            String requestBody = OBJECT_MAPPER.writeValueAsString(request);
            HttpResponse<String> httpResponse = sendPostRequest(ORDERS_URL, requestBody);

            //응답 처리
            if (httpResponse.statusCode() == 200) {
                return OBJECT_MAPPER.readValue(httpResponse.body(), OrderResponse.class);
            } else {
                log.error("API 응답 실패");
                return null;
            }
        } catch (Exception e) {
            log.error("예외 발생 >> {}", e.getMessage());
            return null;
        }
    }

    /**
     * 주문 생성 요청
     */
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

    /**
     * 상품 페이징 조회
     */
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