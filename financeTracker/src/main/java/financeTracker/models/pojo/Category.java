package financeTracker.models.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String type;
    @OneToMany(mappedBy = "category")
    private List<PlannedPayment> plannedPayments;
    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;
    @OneToOne(mappedBy = "category")
    private CategoryImage image;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
