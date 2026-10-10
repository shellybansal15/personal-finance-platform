package com.finance.dao;

import com.finance.exception.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [Collections & Generics Rubric: Generic BaseDAO<T> Interface]
 * Standard generic Data Access Object interface defining foundational CRUD
 * operations. Uses parameterized type <T> and Java Collection List<T>.
 *
 * @param <T> The entity type handled by the implementing DAO
 * ============================================================================
 */
public interface BaseDAO<T> {

    /**
     * Retrieves an entity by its unique primary key ID.
     *
     * @param id Primary key ID
     * @return Optional containing the entity if found, or empty Optional
     * @throws DatabaseException on SQL failure
     */
    Optional<T> findById(int id) throws DatabaseException;

    /**
     * Retrieves all records of type T from the corresponding database table.
     * Demonstrates Collections: List<T>.
     *
     * @return List of all entities
     * @throws DatabaseException on SQL failure
     */
    List<T> findAll() throws DatabaseException;

    /**
     * Persists a new entity instance into the database.
     *
     * @param entity Entity to insert
     * @return true if row was successfully inserted, false otherwise
     * @throws DatabaseException on SQL failure
     */
    boolean save(T entity) throws DatabaseException;

    /**
     * Updates an existing database row corresponding to the entity.
     *
     * @param entity Entity containing updated values
     * @return true if row was updated, false otherwise
     * @throws DatabaseException on SQL failure
     */
    boolean update(T entity) throws DatabaseException;

    /**
     * Deletes a record from the database by its primary key ID.
     *
     * @param id Primary key ID
     * @return true if row was deleted, false otherwise
     * @throws DatabaseException on SQL failure
     */
    boolean delete(int id) throws DatabaseException;
}
