package financeTracker.models.repository;

import financeTracker.models.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlannedPaymentsRepository extends JpaRepository<PlannedPayment, Integer> {
    PlannedPayment findPlannedPaymentByNameAndAccountId(String name, int accountId);
    PlannedPayment findPlannedPaymentByIdAndAccountIdAndOwnerId(int plannedPaymentId, int accountId, int userId);
    List<PlannedPayment> findAllByAccountId(int accountId);
}
