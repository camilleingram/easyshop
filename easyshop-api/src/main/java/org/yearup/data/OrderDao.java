package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

public interface OrderDao {

    Order addToOrders(Profile profile);

    void addToOrderLineItems(ShoppingCart cart, Order order);
}
