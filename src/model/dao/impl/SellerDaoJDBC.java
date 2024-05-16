package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new seller into the database.
     *
     * @param obj The seller object to be inserted. The seller's ID, name, email,
     *            birth date, base salary,
     *            and department ID should be set in the object before calling this
     *            method.
     * @throws DbException If an error occurs while executing the SQL query or if no
     *                     rows are affected.
     */
    @Override
    public void insert(Seller obj) {

        PreparedStatement st = null;

        try {
            // Prepare the SQL statement to insert a new seller into the database
            st = conn.prepareStatement(
                    "INSERT INTO seller "
                            + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                            + "VALUES "
                            + "(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            // Set the parameters of the SQL statement
            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());

            // Execute the SQL statement and get the number of rows affected
            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {

                // If rows were affected, get the generated keys (in this case, the seller's ID)
                ResultSet rs = st.getGeneratedKeys();

                if (rs.next()) {
                    // Retrieves the generated seller's ID from the result set and sets it in the
                    // seller object.
                    int id = rs.getInt(1);
                    // Set the seller's ID in the object
                    obj.setId(id);
                }
                // Close the ResultSet to free up resources
                DB.closeResultSet(rs);
            } else {
                // If no rows were affected, throw a custom exception
                throw new DbException("Unexpected error! No rows affected");
            }
        } catch (SQLException e) {
            // If an error occurs while executing the SQL query, throw a custom exception
            throw new DbException(e.getMessage());
        } finally {
            // Close the PreparedStatement to free up resources
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Seller obj) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    /**
     * This method retrieves a seller by their unique identifier.
     *
     * @param id The unique identifier of the seller to retrieve.
     * @return The seller with the given identifier, or null if no seller is found.
     * @throws DbException If an error occurs while executing the SQL query.
     */
    @Override
    public Seller findById(Integer id) {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            // Prepare the SQL statement to retrieve a seller by their unique identifier
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "

                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE seller.Id =?");

            // Set the seller ID parameter in the SQL statement
            st.setInt(1, id);

            // Execute the SQL query and retrieve the result set
            rs = st.executeQuery();

            // If a seller is found, instantiate a Seller object and return it
            if (rs.next()) {

                Department dep = instantiateDepartment(rs);
                Seller obj = instantiateSeller(rs, dep);
                return obj;

            }

            // If no seller is found, return null
            return null;

        } catch (SQLException e) {

            // If an error occurs while executing the SQL query, throw a custom exception
            throw new DbException(e.getMessage());

        } finally {

            // Close the statement and result set to free up resources
            DB.closeStatement(st);
            DB.closeResultSet(rs);

        }
    }

    /**
     * This method instantiates a Seller object from the given ResultSet and a
     * Department object.
     *
     * @param rs  The ResultSet containing the seller data.
     * @param dep The Department object associated with the seller.
     * @return The Seller object instantiated from the ResultSet and Department
     *         object.
     * @throws SQLException If an error occurs while retrieving data from the
     *                      ResultSet.
     */
    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setDepartment(dep);
        return obj;
    }

    /**
     * This method instantiates a Department object from the given ResultSet.
     *
     * @param rs The ResultSet containing the department data.
     * @return The Department object instantiated from the ResultSet.
     * @throws SQLException If an error occurs while retrieving data from the
     *                      ResultSet.
     */
    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    /**
     * This method retrieves all sellers from the database.
     *
     * @return A list of all sellers, where each seller is associated with a
     *         department.
     * @throws DbException If an error occurs while executing the SQL query.
     */
    @Override
    public List<Seller> findAll() {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            // Prepare the SQL statement to retrieve all seller's information and sort them
            // by name.
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "ORDER BY Name");

            // Execute the SQL query and retrieve the result set
            rs = st.executeQuery();

            // Initialize a list to store the sellers and a map to store unique departments
            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            // Iterate through the result set and create Seller objects
            while (rs.next()) {

                // Retrieve the department from the map or create a new one if it doesn't exist
                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                // Create a Seller object and add it to the list
                Seller obj = instantiateSeller(rs, dep);
                list.add(obj);

            }

            // Return the list of sellers
            return list;

        } catch (SQLException e) {

            // Throw a custom exception if an error occurs while executing the SQL query
            throw new DbException(e.getMessage());

        } finally {

            // Close the statement and result set to free up resources
            DB.closeStatement(st);
            DB.closeResultSet(rs);

        }

    }

    /**
     * This method retrieves a list of sellers associated with a specific
     * department.
     *
     * @param department The department for which to retrieve sellers.
     * @return A list of sellers associated with the given department.
     * @throws DbException If an error occurs while executing the SQL query.
     */
    @Override
    public List<Seller> findByDepartment(Department department) {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {

            // Prepare the SQL statement to retrieve sellers associated with a specific
            // department
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE DepartmentId =? "
                            + "ORDER BY Name");

            // Set the department ID parameter in the SQL statement
            st.setInt(1, department.getId());

            // Execute the SQL query and retrieve the result set
            rs = st.executeQuery();

            // Initialize a list to store the sellers and a map to store unique departments
            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            // Iterate through the result set and create Seller objects
            while (rs.next()) {

                // Retrieve the department from the map or create a new one if it doesn't exist
                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                // Create a Seller object and add it to the list
                Seller obj = instantiateSeller(rs, dep);
                list.add(obj);

            }

            // Return the list of sellers
            return list;

        } catch (SQLException e) {

            // Throw a custom exception if an error occurs while executing the SQL query
            throw new DbException(e.getMessage());

        } finally {

            // Close the statement and result set to free up resources
            DB.closeStatement(st);
            DB.closeResultSet(rs);

        }
    }
}
