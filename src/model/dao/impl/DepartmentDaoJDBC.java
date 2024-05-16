package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

  private Connection conn;

  public DepartmentDaoJDBC(Connection conn) {
    this.conn = conn;
  }

  /**
   * Inserts a new department into the database.
   *
   * @param obj The department object to be inserted.
   * @throws DbException If an error occurs while inserting the department.
   */
  @Override
  public void insert(Department obj) {

    PreparedStatement st = null;

    try {

      // Prepare the SQL statement with placeholders for the department's attributes
      st = conn.prepareStatement(
          "INSERT INTO department "
              + "(Name) "
              + "VALUES "
              + "(?)",
          Statement.RETURN_GENERATED_KEYS);

      // Set the values for the placeholders
      st.setString(1, obj.getName());

      // Execute the SQL statement and get the number of rows affected
      int rows = st.executeUpdate();

      // Check if the insertion was successful
      if (rows > 0) {
        System.out.println("Insert succeeded! Rows affected: " + rows);
      } else {
        System.out.println("Insert failed! Rows: " + rows);
      }

    } catch (SQLException e) {
      // If an error occurs, throw a custom exception
      throw new DbException(e.getMessage());
    } finally {
      // Close the prepared statement to free up resources
      DB.closeStatement(st);
    }
  }

  @Override
  public void update(Department obj) {

    PreparedStatement st = null;

    try {

      st = conn.prepareStatement(
          "UPDATE department " +
              "SET Name = ? "
              + "WHERE Id = ? ");

      st.setString(1, obj.getName());
      st.setInt(2, obj.getId());

      int rows = st.executeUpdate();
      if (rows > 0) {
        System.out.println("Updated " + rows + " rows");
      }

    } catch (SQLException e) {
      throw new DbException("Error executing update! " + e.getMessage());
    } finally {
      DB.closeStatement(st);
    }
  }

  @Override
  public void deleteById(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

  /**
   * Finds a department by its unique identifier in the database.
   *
   * @param id The unique identifier of the department to be found.
   * @return The department object with the specified identifier, or null if no
   *         such department exists.
   * @throws DbException If an error occurs while executing the SQL query or
   *                     handling the result set.
   */
  @Override
  public Department findById(Integer id) {

    PreparedStatement st = null;
    ResultSet rs = null;

    try {

      // Prepare the SQL statement with a placeholder for the department's identifier
      st = conn.prepareStatement(
          "SELECT * FROM department "
              + "WHERE Id =?");

      // Set the value for the placeholder
      st.setInt(1, id);

      // Execute the SQL statement and retrieve the result set
      rs = st.executeQuery();

      // If a row is returned, create a new Department object and populate it with the
      // data from the result set
      if (rs.next()) {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
      }

      // If no row is returned, return null
      return null;

    } catch (SQLException e) {
      // If an error occurs, throw a custom exception
      throw new DbException(e.getMessage());
    } finally {
      // Close the prepared statement and result set to free up resources
      DB.closeStatement(st);
      DB.closeResultSet(rs);
    }
  }

  @Override
  public List<Department> findAll() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

}
