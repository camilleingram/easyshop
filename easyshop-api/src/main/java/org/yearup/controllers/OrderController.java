package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Order;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@CrossOrigin
public class OrderController {

    private final UserDao userDao;
    private OrderDao orderDao;
    private ProfileDao profileDao;
    ShoppingCartDao shoppingCartDao;

    @Autowired
    public OrderController(OrderDao orderDao, ProfileDao profileDao, ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.profileDao = profileDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    public Order makeOrder(Principal principal) {

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        Order order = orderDao.addToOrders(profileDao.getByUserId(userId));
        orderDao.addToOrderLineItems(shoppingCartDao.getByUserId(userId),order);

        return order;

    }
}
