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
              + "(Id, Name) "
              + "VALUES "
              + "(?,?)",
          Statement.RETURN_GENERATED_KEYS);

      // Set the values for the placeholders
      st.setInt(1, obj.getId());
      st.setString(2, obj.getName());

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

  }

  @Override
  public void deleteById(Integer id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

  @Override
  public Department findById(Integer id) {

    PreparedStatement st = null;
    ResultSet rs = null;

    try {

      st = conn.prepareStatement(
          "SELECT * FROM department "
              + "WHERE Id = ?");
      st.setInt(1, id);

      rs = st.executeQuery();

      if (rs.next()) {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
      }
      return null;

    } catch (SQLException e) {
      throw new DbException(e.getMessage());
    } finally {
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
