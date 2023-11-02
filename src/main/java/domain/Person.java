package domain;

import jakarta.persistence.*;

@Table(name = "users")
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nick_name")
    private String name;

    @Column(name = "old")
    private Integer age;

    @Column(nullable = false)
    private String email;

    @Transient
    private Integer index;

    public Person() {

    }

    public Person(Long id, String name, Integer age, String email, Integer index) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.index = index;
    }

    public Person(String name, Integer age, String email, Integer index) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.index = index;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public Integer getIndex() {
        return index;
    }

	public void setName(String name) {
        this.name = name;
	}

    public void setEmail(String email) {
        this.email = email;
    }
}
