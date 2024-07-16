package gift.model;

import jakarta.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(255) not null unique")
    private String name;

    protected Category(){
    }

    public Category(String name) {
        this.setName(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        CategoryName categoryName = new CategoryName(name);
        this.name = categoryName.getName();
    }

}