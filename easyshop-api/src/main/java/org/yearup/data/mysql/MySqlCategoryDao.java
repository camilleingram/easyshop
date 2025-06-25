package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    private final DataSource dataSource;
    private final ProductDao productDao;

    public MySqlCategoryDao(DataSource dataSource, ProductDao productDao)
    {
        super(dataSource);
        this.dataSource = dataSource;
        this.productDao = productDao;
    }

    @Override
    public List<Category> getAllCategories() {
        // get all categories
        List<Category> categories = new ArrayList<>();

        String query = "SELECT * FROM categories";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet row = preparedStatement.executeQuery();

            while(row.next()) {
                 Category category = mapRow(row);
                 categories.add(category);
            }

        }catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {

        String query = "SELECT * " +
                "FROM categories " +
                "WHERE category_id = ?";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, categoryId);

            ResultSet row = preparedStatement.executeQuery();

            if(row.next()) {
                return mapRow(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        String query = "INSERT INTO categories(name, description) " +
                "VALUES(?, ?)";

        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            int rows = preparedStatement.executeUpdate();

            if(rows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if(generatedKeys.next()) {
                    int newCategoryId = generatedKeys.getInt(1);

                    return getById(newCategoryId);
                }
            }

        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category) {
        String query = "UPDATE categories " +
                "SET name = ?, description = ? " +
                "WHERE category_id = ?";

        try(Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
