package test.org.hrodberaht.inject.extension.ejbunit.ejb3.service;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ¤Projectname¤
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:29:52
 * @version 1.0
 * @since 1.0
 */
@Stateless
public class EJB3InnerServiceImpl implements EJB3InnerServiceInterface {

    @Resource(name = "DataSource")
    private DataSource dataSource;

    public void doSomething() {
        System.out.print("Hi there Inner");
    }

    public String findSomething(Long id) {
        return "Something Deep";
    }

    public String findSomethingFromDataSource(Long id) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from the_table where id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            String message = null;
            if(resultSet.next()){
                message = resultSet.getString("name");
            }
            // This is not proper socket close handling and will leak in case of errors, but this is a simple test
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return message;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }        
    }


}
