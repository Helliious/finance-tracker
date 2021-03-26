package financeTracker.models.repository;

import financeTracker.models.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlannedPaymentsRepository extends JpaRepository<PlannedPayment, Integer> {

}
