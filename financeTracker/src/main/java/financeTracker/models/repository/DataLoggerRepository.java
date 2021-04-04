package financeTracker.models.repository;

import financeTracker.models.pojo.DataLogger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataLoggerRepository extends JpaRepository<DataLogger, Integer> {

}
