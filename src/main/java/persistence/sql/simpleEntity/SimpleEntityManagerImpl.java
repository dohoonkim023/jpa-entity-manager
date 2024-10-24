package persistence.sql.simpleEntity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.FindByIdQuery;
import persistence.sql.dml.GenericRowMapper;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.UpdateQuery;

public class SimpleEntityManagerImpl<T, ID> implements SimpleEntityManager<T, ID> {

    private final Class<T> entityClass;
    private final JdbcTemplate jdbcTemplate;

    public SimpleEntityManagerImpl(Class<T> entityClass, JdbcTemplate jdbcTemplate) {
        this.entityClass = entityClass;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public T findById(ID id) {
        String sql = new FindByIdQuery<>(entityClass, id).generateQuery();
        return jdbcTemplate.queryForObject(sql, new GenericRowMapper<>(entityClass));
    }

    @Override
    public void persist(T entity) throws IllegalAccessException {
        String sql = new InsertQuery<>(entity).generateQuery();
        jdbcTemplate.execute(sql);
    }

    @Override
    public void remove(T entity) {
        String sql = new DeleteQuery<>(entity).generateQuery();
        jdbcTemplate.execute(sql);
    }

    @Override
    public void update(T entity) throws IllegalAccessException {
        String sql = new UpdateQuery<>(entity).generateQuery();
        jdbcTemplate.execute(sql);
    }

}