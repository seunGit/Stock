package com.example.stocks.facade;

import com.example.stocks.repository.LockRepository;
import com.example.stocks.service.StockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NamedLockStockFacade {
    private final LockRepository lockRepository;

    private final StockService stockService;

    public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) {
        try {
            // 락 설정
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity);
        } finally {
            // 락 해제
            lockRepository.releaseLock(id.toString());
        }
    }
}
