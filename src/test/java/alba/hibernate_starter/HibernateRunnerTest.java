package alba.hibernate_starter;

import alba.hibernate_starter.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class HibernateRunnerTest {

    @Test
    public void testHibernateApi() throws SQLException, IllegalAccessException {
        var user = User.builder()
                .username("alba131213@mail.ru")
                .birthdate(LocalDate.of(1998,12,13))
                .lastname("khakimov")
                .firstname("alba")
                .age(25)
                .build();
        var sql = """
                insert into
                %s
                (%s)
                values
                (%s)
                """;

       var tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
               .map(table -> table.schema() + "." + table.name())
               .orElse(user.getClass().getName());

       Field[] fields = user.getClass().getDeclaredFields();

       var coulmnNames = Arrays.stream(fields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName())
                ).collect(Collectors.joining(", "));

       var coulmnValues = Arrays.stream(fields)
               .map(field -> "?")
               .collect(Collectors.joining(", "));

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "postgres","123");

        PreparedStatement preparedStatement = connection
                .prepareStatement(sql.formatted(tableName,coulmnNames,coulmnValues));

        for (int i =0;i<fields.length;i++){
            fields[i].setAccessible(true);
            preparedStatement.setObject(i + 1, fields[i].get(user));

        }

        System.out.println(preparedStatement);
        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();

    }
}