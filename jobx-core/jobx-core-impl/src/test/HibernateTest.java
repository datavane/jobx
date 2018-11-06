import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HibernateTest {

    @Test
    public void alterTable() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/jobx?useUnicode=true&characterEncoding=utf8&useCursorFetch=true&autoReconnect=true&failOverReadOnly=false";
        Connection connection = DriverManager.getConnection(url,"root","123322242");
        connection.setAutoCommit(true);

        String sql = "alter table t_agent change `stauts` `stauts` int;";
        connection.prepareStatement(sql).execute();
    }

}
