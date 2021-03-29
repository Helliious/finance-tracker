package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlannedPaymentsRepository extends JpaRepository<PlannedPayment, Integer> {
    PlannedPayment findPlannedPaymentByNameAndAccountId(String name, int accountId);
    PlannedPayment findPlannedPaymentByIdAndAccountIdAAndOwnerId(int plannedPaymentId, int accountId, int userId);
}
