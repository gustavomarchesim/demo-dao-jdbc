package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

  /**
   * Updates an existing department in the database.
   *
   * @param obj The department object to be updated. The object must contain the
   *            department's unique identifier (Id) and the new name.
   * @throws DbException If an error occurs while executing the SQL query or
   *                     handling the result set.
   */
  @Override
  public void update(Department obj) {

    PreparedStatement st = null;

    try {

      // Prepare the SQL statement with placeholders for the department's attributes
      st = conn.prepareStatement(
          "UPDATE department " +
              "SET Name =? " +
              "WHERE Id =? ");

      // Set the values for the placeholders
      st.setString(1, obj.getName());
      st.setInt(2, obj.getId());

      // Execute the SQL statement and get the number of rows affected
      int rows = st.executeUpdate();

      // Check if the update was successful
      if (rows > 0) {
        System.out.println("Updated " + rows + " rows");
      }

    } catch (SQLException e) {
      // If an error occurs, throw a custom exception
      throw new DbException("Error executing update! " + e.getMessage());
    } finally {
      // Close the prepared statement to free up resources
      DB.closeStatement(st);
    }
  }

  /**
   * Deletes a department from the database by its unique identifier.
   *
   * @param id The unique identifier of the department to be deleted.
   * @throws DbException If an error occurs while executing the SQL query or
   *                     handling the result set.
   *                     If the department with the specified id does not exist,
   *                     throws an exception.
   */
  @Override
  public void deleteById(Integer id) {

    PreparedStatement st = null;

    try {
      // Prepare the SQL statement to delete a department by its unique identifier.
      st = conn.prepareStatement(
          "DELETE FROM department "
              + "WHERE Id =?");
      st.setInt(1, id);
      // Execute the SQL statement and get the number of rows affected
      int rows = st.executeUpdate();

      // If no rows were deleted, it means the department with the specified id does
      // not exist.
      if (rows == 0) {
        throw new DbException("Unexpected error! Id not found!");
      }
      System.out.println("Success! ID deleted!");

    } catch (SQLException e) {
      // If an error occurs, throw a custom exception with the error message.
      throw new DbException(e.getMessage());
    } finally {
      // Close the prepared statement to free up resources.
      DB.closeStatement(st);
    }
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

  /**
   * Retrieves a list of all departments from the database.
   *
   * @return A list of Department objects, or an empty list if no departments
   *         exist.
   * @throws DbException If an error occurs while executing the SQL query or
   *                     handling the result set.
   */
  @Override
  public List<Department> findAll() {

    PreparedStatement st = null;
    ResultSet rs = null;

    try {

      // Prepare the SQL statement to select all departments ordered by name
      st = conn.prepareStatement(
          "SELECT * FROM department "
              + " ORDER BY Name");

      // Execute the SQL statement and retrieve the result set
      rs = st.executeQuery();

      // Create a new ArrayList to store the Department objects
      List<Department> list = new ArrayList<>();

      // Iterate through the result set and create Department objects for each row
      while (rs.next()) {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));

        // Add the Department object to the list
        list.add(dep);
      }

      // Return the list of Department objects
      return list;

    } catch (Exception e) {
      // If an error occurs, throw a custom exception with the error message
      throw new DbException(e.getMessage());
    } finally {
      // Close the prepared statement and result set to free up resources
      DB.closeStatement(st);
      DB.closeResultSet(rs);
    }
  }
}
