package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here

    void addToCart(int productId, int userId);

    void updateItem(int productId, int quantity, int userId);

    void clearCart(int userId);
}
