package persistence.sql.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.domain.Person;
import persistence.sql.entity.EntityManager;
import persistence.sql.entity.EntityManagerImpl;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityManagerImplTest {

    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void init() throws SQLException {
        final DatabaseServer server = new H2();
        server.start();

        CreateQueryBuilder queryBuilder = new CreateQueryBuilder(Person.class);
        String tableQuery = queryBuilder.createTableQuery(Person.class);
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(Person.class);
        Person person = new Person("yang", 23, "rhfp@naver.com", 3);
        String insertQuery = insertQueryBuilder.getInsertQuery(person);

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(tableQuery);
        jdbcTemplate.execute(insertQuery);

        entityManager = new EntityManagerImpl(server.getConnection());
    }

    @AfterEach
    void afterEach() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder();
        String dropTableQuery = dropQueryBuilder.dropTableQuery(Person.class);
        jdbcTemplate.execute(dropTableQuery);
    }

    @Test
    @DisplayName("EntityManager의 find구현")
    void entityManager_find() {
        Person expectPerson = new Person(1L, "yang", 23, "rhfp@naver.com");

        Person resultPerson = entityManager.find(Person.class, 1L);

        assertAll(
                () -> assertThat(expectPerson.getAge()).isEqualTo(resultPerson.getAge()),
                () -> assertThat(expectPerson.getEmail()).isEqualTo(resultPerson.getEmail()),
                () -> assertThat(expectPerson.getId()).isEqualTo(resultPerson.getId()),
                () -> assertThat(expectPerson.getIndex()).isEqualTo(resultPerson.getIndex()),
                () -> assertThat(expectPerson.getName()).isEqualTo(resultPerson.getName())
        );
    }

    @Test
    @DisplayName("EntityManager의 persist구현")
    void entityManager_persist() {
        Person expectPerson = new Person(2L, "yang2", 25, "rhfpdk92@naver.com");

        entityManager.persist(expectPerson);

        Person resultPerson = entityManager.find(Person.class, 2L);

        assertAll(
                () -> assertThat(expectPerson.getAge()).isEqualTo(resultPerson.getAge()),
                () -> assertThat(expectPerson.getEmail()).isEqualTo(resultPerson.getEmail()),
                () -> assertThat(expectPerson.getId()).isEqualTo(resultPerson.getId()),
                () -> assertThat(expectPerson.getIndex()).isEqualTo(resultPerson.getIndex()),
                () -> assertThat(expectPerson.getName()).isEqualTo(resultPerson.getName())
        );
    }


    @Test
    @DisplayName("EntityManager의 remove구현")
    void entityManager_remove() {
        Person expectPerson = new Person(2L, "yang2", 25, "rhfpdk92@naver.com");

        entityManager.persist(expectPerson);
        entityManager.remove(expectPerson);

        assertThrows(RuntimeException.class, () -> entityManager.find(Person.class, 2L));
    }

    @Test
    @DisplayName("EntityManager의 update구현")
    void entityManager_update() {
        Person person = new Person(2L, "yang2", 25, "rhfpdk92@naver.com");
        Person updatePerson = new Person(2L, "yang3", 25, "rhfpdk92@gmail.com");

        entityManager.persist(person);
        entityManager.update(updatePerson);
        Person resultPerson = entityManager.find(Person.class, 2L);

        assertAll(
                () -> assertThat(updatePerson.getAge()).isEqualTo(resultPerson.getAge()),
                () -> assertThat(updatePerson.getEmail()).isEqualTo(resultPerson.getEmail()),
                () -> assertThat(updatePerson.getId()).isEqualTo(resultPerson.getId()),
                () -> assertThat(updatePerson.getIndex()).isEqualTo(resultPerson.getIndex()),
                () -> assertThat(updatePerson.getName()).isEqualTo(resultPerson.getName())
        );

    }
}
