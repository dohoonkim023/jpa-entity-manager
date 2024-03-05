package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.EntityMetaData;
import domain.Person3;
import domain.dialect.Dialect;
import domain.dialect.H2Dialect;
import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityLoaderImplTest {

    static Dialect dialect = new H2Dialect();
    static EntityMetaData entityMetaData = new EntityMetaData(Person3.class);

    static DatabaseServer server;
    static JdbcTemplate jdbcTemplate;
    static EntityPersister entityPersister;
    static EntityLoader entityLoader;
    static SimpleEntityManager simpleEntityManager;

    Person3 person;

    @BeforeAll
    static void init() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityPersister = new EntityPersisterImpl(jdbcTemplate, dialect, entityMetaData);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, entityMetaData);
        simpleEntityManager = new SimpleEntityManager(entityPersister, entityLoader);
    }

    @BeforeEach
    void setUp() {
        person = new Person3(1L, "test", 20, "test@test.com");
        createTable();
    }

    @AfterEach
    void remove() {
        dropTable();
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }

    @DisplayName("findById 테스트")
    @Test
    void findByIdTest() {
        entityPersister.insert(person);
        Person3 person3 = entityLoader.findById(person.getClass(), person.getId());
        assertAll(
                () -> assertThat(person3.getId()).isEqualTo(person.getId()),
                () -> assertThat(person3.getName()).isEqualTo(person.getName()),
                () -> assertThat(person3.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(person3.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @DisplayName("findAll 테스트")
    @Test
    void findAllTest() {
        entityPersister.insert(person);

        person = new Person3(2L, "test2", 22, "test2@test.com");
        entityPersister.insert(person);

        person = new Person3(3L, "test3", 23, "test3@test.com");
        entityPersister.insert(person);

        assertThat(entityLoader.findAll(person.getClass())).hasSize(3);
    }

    private void createTable() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(dialect, entityMetaData);
        jdbcTemplate.execute(createQueryBuilder.createTable(person));
    }

    private void dropTable() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(entityMetaData);
        jdbcTemplate.execute(dropQueryBuilder.dropTable());
    }
}