package financeTracker.services;

import financeTracker.models.dao.DataLoggerDAO;
import financeTracker.models.pojo.DataLogger;
import financeTracker.models.repository.DataLoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataLoggerService {
    @Autowired
    private DataLoggerDAO dataLoggerDAO;
    @Autowired
    private DataLoggerRepository dataLoggerRepository;

    @Scheduled(fixedRate = 10000)
    public void logData() {
        List<DataLogger> logs = dataLoggerDAO.logData();
        if (!logs.isEmpty()) {
            for (DataLogger d : logs) {
                dataLoggerRepository.save(d);
            }
        }
    }
}
