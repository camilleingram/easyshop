package org.yearup.data.mysql;


import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {


    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String query = "SELECT shopping_cart.user_id, shopping_cart.product_id, shopping_cart.quantity, products.product_id," +
                "products.name, products.price, products.category_id, products.description, products.color,products.stock," +
                "products.image_url, products.featured " +
                "FROM shopping_cart " +
                "JOIN products ON shopping_cart.product_id = products.product_id " +
                "WHERE shopping_cart.user_id = ?";

        try(Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);


            ResultSet row = preparedStatement.executeQuery();

            ShoppingCart cart = new ShoppingCart();

            while(row.next()) {
                Product product = mapProduct(row);
                int quantity = row.getInt("quantity");

                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProduct(product);
                shoppingCartItem.setQuantity(quantity);
                shoppingCartItem.setDiscountPercent(BigDecimal.ZERO);
                cart.add(shoppingCartItem);

            }

            return cart;

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addToCart(int productId, int userId) {
        String query = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                "VALUES(?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1";

        try(Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);

            preparedStatement.executeUpdate();


        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateItem(int productId, int quantity, int userId) {
        String query = "UPDATE shopping_cart " +
                "SET quantity = ? " +
                "WHERE product_id = ? AND user_id = ?";

        try(Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, userId);

            preparedStatement.executeUpdate();
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCart(int userId) {
        String query = "DELETE FROM shopping_cart " +
                "WHERE user_id = ?";

        try(Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    private Product mapProduct(ResultSet row) throws SQLException {

        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        String imageUrl = row.getString("image_url");
        int stock = row.getInt("stock");
        boolean featured = row.getBoolean("featured");


        Product addedProduct = new Product(productId, name, price, categoryId, description, color, stock, featured, imageUrl);

        return addedProduct;
    }
}
