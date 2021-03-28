package financeTracker.models.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "category_images")
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
