package financeTracker.models.dao;
import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.budget_dto.BudgetWithoutAccountAndOwnerDTO;
import financeTracker.models.dto.budget_dto.FilterBudgetRequestDTO;
import financeTracker.models.pojo.Account;
import financeTracker.models.pojo.Budget;
import financeTracker.models.pojo.Category;
import financeTracker.models.pojo.User;
import financeTracker.models.repository.AccountRepository;
import financeTracker.models.repository.CategoryRepository;
import financeTracker.models.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class BudgetDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountRepository accountRepository;

    public ArrayList<BudgetWithoutAccountAndOwnerDTO> filterBudget(int userId, FilterBudgetRequestDTO dto){
        ArrayList<BudgetWithoutAccountAndOwnerDTO> budgetsWithoutAccountAndOwnerDTOS =new ArrayList<>();
        
        String sql="SELECT* FROM budgets WHERE owner_id = ? ";
        int numberOfParameters=1;
        boolean nameIncludedInFilter=false;
        boolean categoryIncludedInFilter=false;
        boolean bothAmountsIncluded=false;
        boolean amountFromIncluded=false;
        boolean amountToIncluded=false;
        boolean bothDatesIncluded=false;
        boolean dateFromIncluded=false;
        boolean dateToIncluded=false;
        if (dto.getName()!=null){
            sql+="AND name LIKE ?";
            nameIncludedInFilter=true;
            numberOfParameters++;
        }
        if (dto.getCategoryId()>0){
            sql+="AND category_id= ? ";
            categoryIncludedInFilter=true;
            numberOfParameters++;
        }
        if (dto.getAmountFrom()> dto.getAmountTo()){
            throw new BadRequestException("Amount from can't be bigger than Amount to");
        }
        if (dto.getAmountFrom()>0&&dto.getAmountTo()>0){
            sql+="AND amount BETWEEN ? AND ? ";
            bothAmountsIncluded=true;
            numberOfParameters+=2;
        }
        else {
            if (dto.getAmountFrom() > 0 && dto.getAmountTo() <= 0) {
                sql += "AND amount > ? ";
                amountFromIncluded = true;
                numberOfParameters++;
            }
            if (dto.getAmountFrom() < 0 && dto.getAmountTo() > 0) {
                amountToIncluded = true;
                sql += "AND amount< ? ";
                numberOfParameters++;
            }
        }
        if(dto.getDateFrom()!=null&&dto.getDateTo()!=null){
            sql+="AND create_time BETWEEN ? AND ?";
            numberOfParameters+=2;
            bothDatesIncluded=true;
        }
        else{
            if (dto.getDateFrom()!=null &&dto.getDateTo()==null){
                sql+="AND create_time >= ?";
                numberOfParameters++;
                dateFromIncluded=true;
            }
            if (dto.getDateFrom()==null&&dto.getDateTo()==null){
                sql+="AND create_time <= ?";
                numberOfParameters++;
                dateToIncluded=true;
            }
        }
        System.out.println(sql);
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            for (int i = 2; i <= numberOfParameters; i++) {
                if (nameIncludedInFilter) {
                    ps.setString(i, dto.getName()+"%");
                    nameIncludedInFilter = false;
                } else if (categoryIncludedInFilter) {
                    System.out.println(i + " setted " + dto.getCategoryId());
                    ps.setInt(i, dto.getCategoryId());
                    categoryIncludedInFilter = false;
                } else if (bothAmountsIncluded) {
                    ps.setDouble(i, dto.getAmountFrom());
                    ps.setDouble(++i, dto.getAmountTo());
                    bothAmountsIncluded = false;
                } else if (amountFromIncluded) {
                    ps.setDouble(i, dto.getAmountFrom());
                    amountFromIncluded = false;
                } else if (amountToIncluded) {
                    ps.setDouble(i, dto.getAmountTo());
                    amountToIncluded = false;
                }else if(bothDatesIncluded){
                    ps.setTimestamp(i,dto.getDateFrom());
                    ps.setTimestamp(++i,dto.getDateTo());
                    bothAmountsIncluded=false;
                }
                else if(dateFromIncluded){
                    ps.setTimestamp(i,dto.getDateFrom());
                    dateFromIncluded=false;
                }
                else if(dateToIncluded){
                    ps.setTimestamp(i,dto.getDateTo());
                    dateToIncluded=false;
                }
            }
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                do{
                    Optional<Account> optionalAccount = accountRepository.findById(result.getInt("account_id"));
                    Optional<Category> optionalCategory = categoryRepository.findById(result.getInt("category_id"));
                    Optional<User> optionalUser = userRepository.findById(result.getInt("owner_id"));
                    Budget budget = new Budget(result.getInt("id"),
                            result.getString("name"),
                            result.getString("label"),
                            result.getDouble("amount"),
                            result.getTimestamp("due_time"),
                            optionalAccount.get(),
                            optionalUser.get(),
                            optionalCategory.get()
                    );
                    budgetsWithoutAccountAndOwnerDTOS.add(new BudgetWithoutAccountAndOwnerDTO(budget));
                }while (result.next());
            }
            else{
                throw new NotFoundException("There is not budgets corresponding to current filter");
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
        return budgetsWithoutAccountAndOwnerDTOS;
    }
}
