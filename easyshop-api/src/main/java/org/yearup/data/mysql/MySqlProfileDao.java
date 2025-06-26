package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.security.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    private final UserDao userDao;

    public MySqlProfileDao(DataSource dataSource, UserDao userDao)
    {
        super(dataSource);
        this.userDao = userDao;
    }

    @Override
    public Profile create(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId) {

        String query = "SELECT * " +
                "FROM profiles " +
                "WHERE user_id = ?";

        try (Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            ResultSet row = preparedStatement.executeQuery();

            if (row.next()) {

                return mapProfile(row, userId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void updateProfile(int userId, String param, String value) {

        List<String> updatedParams = List.of("first_name", "last_name", "phone", "email", "address", "city", "state", "zip");

        if (!updatedParams.contains(param)) {
            throw new IllegalArgumentException("Invalid column name: " + param);
        }


        String query = "UPDATE profiles SET " + param + " = ? WHERE user_id = ?";

        try(Connection connection = getConnection()) {


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, value);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Profile mapProfile(ResultSet row, int userId) throws SQLException {

        String firstName = row.getString("first_name");
        String lastName = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");

        return new Profile(userId, firstName, lastName, phone, email, address, city, state, zip);
    }
}
