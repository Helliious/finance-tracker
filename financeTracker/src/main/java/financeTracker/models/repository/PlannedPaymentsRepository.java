package financeTracker.models.repository;

import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlannedPaymentsRepository extends JpaRepository<PlannedPayment, Integer> {
    PlannedPayment findPlannedPaymentByNameAndAccount(String name, Account account);
    PlannedPayment findPlannedPaymentByIdAndAccount_Id(int plannedPaymentId, int accountId);
}
