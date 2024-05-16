package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {
  public static void main(String[] args) {

    Department dep = new Department();
    DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

    System.out.println("\n=== TEST 1: Department Insert ===");
    dep = new Department(10, "Teste5");
    departmentDao.insert(dep);

    System.out.println("\n=== TEST 2: Department findById ===");
    dep = departmentDao.findById(10);
    System.out.println(dep);

  }

}
