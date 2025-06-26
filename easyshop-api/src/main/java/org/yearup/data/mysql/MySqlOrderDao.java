package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {



    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order addToOrders(Profile profile) {
        String query = "INSERT INTO orders(user_id, date, address, city, state, zip, shipping_amount) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, profile.getUserId());
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(3, profile.getAddress());
            preparedStatement.setString(4, profile.getCity());
            preparedStatement.setString(5, profile.getState());
            preparedStatement.setString(6, profile.getZip());
            preparedStatement.setBigDecimal(7, BigDecimal.ZERO);

            int row = preparedStatement.executeUpdate();
            Order order = new Order();

            if(row > 0) {

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if(generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);
                    order.setUserId(profile.getUserId());
                    order.setDate(LocalDate.now());
                    order.setAddress(profile.getAddress());
                    order.setCity(profile.getCity());
                    order.setState(profile.getState());
                    order.setZip(profile.getZip());
                    order.setShippingAmount(BigDecimal.ZERO);

                }
            }

            return order;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void addToOrderLineItems(ShoppingCart cart, Order order) {
        String query = "INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)" +
                "VALUES(?, ?, ?, ?, ?)";

        Map<Integer, ShoppingCartItem> items = cart.getItems();

        try(Connection connection = getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            for (ShoppingCartItem item : items.values()) {
                preparedStatement.setInt(1, order.getOrderId());
                preparedStatement.setInt(2, item.getProductId());
                preparedStatement.setBigDecimal(3, item.getProduct().getPrice());
                preparedStatement.setInt(4, item.getQuantity());
                preparedStatement.setBigDecimal(5, item.getDiscountPercent());

                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if(generatedKeys.next()) {
                    OrderLineItem lineItem = new OrderLineItem();
                    lineItem.setOrderLineItemId(generatedKeys.getInt(1));
                    lineItem.setOrderId(order.getOrderId());
                    lineItem.setProductId(item.getProductId());
                    lineItem.setSalesPrice(item.getProduct().getPrice());
                    lineItem.setQuantity(item.getQuantity());
                    lineItem.setDiscount(item.getDiscountPercent());
                    order.add(lineItem);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
