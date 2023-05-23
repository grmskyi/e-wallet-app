package com.api.ewalletTask.repositories;

import com.api.ewalletTask.models.Customer;
import com.api.ewalletTask.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.timestamp >= :startOfDay AND t.type = 'Withdrawal'")
    double getDailyWithdrawTotal(@Param("walletId") Long walletId, @Param("startOfDay") Date startOfDay);


    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t WHERE t.wallet.id = :walletId")
    double getTotalAmountForWallet(@Param("walletId") Long walletId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.wallet.customer.id = :customerId AND t.timestamp BETWEEN :startTime AND :endTime")
    int getTransactionsCountWithinHour(@Param("customerId") Long customerId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.type != 'Deposit'")
    double getTotalAmountForWalletExcludingDeposits(@Param("walletId") Long walletId);

    List<Transaction> findByWalletCustomer(Customer customer);
}
