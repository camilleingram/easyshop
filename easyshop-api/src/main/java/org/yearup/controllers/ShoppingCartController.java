package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import java.security.Principal;


// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
@CrossOrigin
public class ShoppingCartController {

    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            if(cart == null) {
                // Return an empty cart instead of null
                cart = new ShoppingCart();
            }

            return cart;
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("products/{productId}")
    public ShoppingCart addToShoppingCart(@PathVariable int productId, Principal principal) {

        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();

            Product product = productDao.getById(productId);
            if(product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }

            shoppingCartDao.addToCart(productId, userId);
            return shoppingCartDao.getByUserId(userId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add item to cart");
        }

    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("products/{productId}")
    public ShoppingCart updateItem(@PathVariable int productId, @RequestBody ShoppingCartItem shoppingCartItem,
            Principal principal) {

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            Product product = productDao.getById(productId);
            if(!cart.contains(productId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart");
            }

            int quantity = shoppingCartItem.getQuantity();

            shoppingCartDao.updateItem(productId, quantity, userId);
            return shoppingCartDao.getByUserId(userId);


        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product quantity");
        }
    }


    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

}
