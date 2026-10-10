package com.finance.dao;

import com.finance.exception.DatabaseException;
import com.finance.model.Category;
import com.finance.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [DAO & JDBC Rubric: CategoryDAO implementing BaseDAO<Category>]
 * Manages category lookups and persistence.
 * ============================================================================
 */
public class CategoryDAO implements BaseDAO<Category> {

    @Override
    public Optional<Category> findById(int id) throws DatabaseException {
        String sql = "SELECT id, name, type FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find category by ID: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<Category> findByName(String name) throws DatabaseException {
        String sql = "SELECT id, name, type FROM categories WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find category by name: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() throws DatabaseException {
        String sql = "SELECT id, name, type FROM categories ORDER BY id ASC";
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all categories", e);
        }
        return list;
    }

    public List<Category> findAllByType(String type) throws DatabaseException {
        String sql = "SELECT id, name, type FROM categories WHERE type = ? ORDER BY id ASC";
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve categories by type: " + type, e);
        }
        return list;
    }

    @Override
    public boolean save(Category category) throws DatabaseException {
        String sql = "INSERT INTO categories (name, type) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getType());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        category.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save category: " + category.getName(), e);
        }
    }

    @Override
    public boolean update(Category category) throws DatabaseException {
        String sql = "UPDATE categories SET name = ?, type = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getType());
            ps.setInt(3, category.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update category ID: " + category.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete category ID: " + id, e);
        }
    }
}
