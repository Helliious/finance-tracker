package financeTracker.models.dao;

import financeTracker.exceptions.BadRequestException;
import financeTracker.exceptions.NotFoundException;
import financeTracker.models.dto.transaction_dto.FilterTransactionRequestDTO;
import financeTracker.models.dto.transaction_dto.TransactionWithoutOwnerDTO;
import financeTracker.models.pojo.*;
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
public class TransactionDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;


    public ArrayList<TransactionWithoutOwnerDTO> filterTransaction(int userId, FilterTransactionRequestDTO dto){
        ArrayList<TransactionWithoutOwnerDTO> transactionsWithoutOwnerDTOS =new ArrayList<>();

        String sql="SELECT* FROM transactions WHERE owner_id = ? ";
        int numberOfParameters=1;
        boolean nameIncludedInFilter=false;
        boolean categoryIncludedInFilter=false;
        boolean bothAmountsIncluded=false;
        boolean amountFromIncluded=false;
        boolean amountToIncluded=false;
        boolean typeIncluded=false;
        if (dto.getName()!=null){
            sql+="AND name LIKE ?";
            nameIncludedInFilter=true;
            numberOfParameters++;
        }
        if (dto.getType()!=null){
            sql+="AND type= ?";
            typeIncluded=true;
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
                } else if (typeIncluded){
                    ps.setString(i,dto.getType());
                    typeIncluded=false;
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
                }
            }
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                do{
                    Optional<Account> optionalAccount = accountRepository.findById(result.getInt("account_id"));
                    Optional<Category> optionalCategory = categoryRepository.findById(result.getInt("category_id"));
                    Optional<User> optionalUser = userRepository.findById(result.getInt("owner_id"));
                    Transaction transaction = new Transaction(result.getInt("id"),
                            result.getString("type"),
                            result.getDouble("amount"),
                            result.getTimestamp("create_time"),
                            result.getString("description"),
                            optionalCategory.get(),
                            optionalAccount.get(),
                            optionalUser.get()
                    );
                    transactionsWithoutOwnerDTOS.add(new TransactionWithoutOwnerDTO(transaction));
                }while (result.next());

            }
            else{
                throw new NotFoundException("There is not transactions corresponding to current filter");
            }
        }
        catch (SQLException e){
            e.getMessage();
        }
        return transactionsWithoutOwnerDTOS;
    }
}
