package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Override
    public void insert(Seller obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void update(Seller obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteById(Integer id) {
        // TODO Auto-generated method stub
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

    @Override
    public List<Seller> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
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
