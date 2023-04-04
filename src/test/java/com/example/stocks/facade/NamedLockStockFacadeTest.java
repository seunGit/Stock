package com.example.stocks.facade;

import static org.junit.jupiter.api.Assertions.*;

import com.example.stocks.domain.Stock;
import com.example.stocks.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
class NamedLockStockFacadeTest {

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public  void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        // 멀티쓰레드 이용
        int threadCount = 100;
        // 비동기로 실행하는 작업을 단순화하여 사용할 수있게 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L,1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 100 - ( 1 * 100 ) = 0
        assertEquals(0L, stock.getQuantity());
    }
}