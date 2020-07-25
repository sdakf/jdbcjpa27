package pl.sda;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class JDBCMain {
    public static void main(String[] args) {
        //createStatement("2000");
//        createStatement("2000 or 1=1");
//        createPreparedStatement("2000", "MANAGER");
//        createPreparedStatement("2000", "ANALYST", "S%");
        createCallableStatement(7369);
    }

    private static void createCallableStatement(int empId) {
        try {
            Connection connection = createConnection()
                    .orElseThrow(() -> new RuntimeException("Nie udało się utworzyć połączenia"));
            String sql = "{call sdajdbc.getEmpName (?, ?)}";
            CallableStatement stmt = connection.prepareCall(sql);
            int empID = empId;
            stmt.setInt(1, empID);
            stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
            stmt.execute();
            String empName = stmt.getString(2);
            System.out.println(empName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void createStatement(String salary) {
        try {
            Connection connection = createConnection()
                    .orElseThrow(() -> new RuntimeException("Nie udało się utworzyć połączenia"));
            String query = "select ename, job, sal, mgr from sdajdbc.employee where sal >=" + salary;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String ename = resultSet.getString("ename");
                String job = resultSet.getString("job");
                BigDecimal sal = resultSet.getBigDecimal("sal");
                Integer mgr = resultSet.getInt("mgr");
                if (resultSet.wasNull()) {
                    mgr = null;
                }
                System.out.println(ename + " " + job + " " + sal + " " + mgr);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void createPreparedStatement(String salaryParam, String jobParam, String nameParam) {
        try {
            Connection connection = createConnection()
                    .orElseThrow(() -> new RuntimeException("Nie udało się utworzyć połączenia"));
            String query = "select ename, job, sal from sdajdbc.employee where sal >= ? and job = ? and ename like ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBigDecimal(1, new BigDecimal(salaryParam));
            preparedStatement.setString(2, jobParam);
            preparedStatement.setString(3, nameParam);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String ename = resultSet.getString("ename");
                String job = resultSet.getString("job");
                BigDecimal sal = resultSet.getBigDecimal("sal");
                System.out.println(ename + " " + job + " " + sal);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static Optional<Connection> createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3308?serverTimezone=UTC",
                    "root",
                    "pass");
        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
        ;
        return Optional.ofNullable(connection);
    }


}
