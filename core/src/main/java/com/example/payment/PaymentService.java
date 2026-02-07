package com.example.payment;

import com.example.infra.redis.CacheService;
import com.example.infra.redis.DistributedLock;
import com.example.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CacheService cacheService;

    @DistributedLock(
            key = "'payment:' + #request.oderId",
            waitTime = 3,
            leaseTime = 30
    )
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) throws InterruptedException {

        //멱등성 체크
        String idempotencyKey = "payment:processed:" + request.orderId();
        String processedPaymentId = cacheService.get(idempotencyKey);

        if (processedPaymentId != null) {
            return PaymentResponse.alreadyProcessed(processedPaymentId);
        }

        //결제 상태 확인
        Payment payment = paymentRepository.findByOrderId(request.orderId())
                .orElseThrow(()->new IllegalArgumentException("존재 하지 않습니다."));

        if(payment.getStatus() == PaymentStatus.COMPLETED){
            throw new IllegalStateException("이미 처리된 결제");
        }

        //결제 처리 (외부 PG 호출)
        log.info("결제 API Call....");
        Thread.sleep(3000);
        String paymentId = UUID.randomUUID().toString(); // 임시

        //4. 상태 업데이트
        payment.complete(paymentId);
        paymentRepository.save(payment);

        // 5. 멱등성 키 저장 (24시간 유지)
        cacheService.saveIdempotencyKey(idempotencyKey,paymentId, Duration.ofDays(1));

        return PaymentResponse.success(paymentId);
    }
}
