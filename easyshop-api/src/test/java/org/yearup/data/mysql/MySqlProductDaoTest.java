package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Product;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MySqlProductDaoTest
{
    private DataSource mockDataSource;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MySqlProductDao dao;

    @BeforeEach
    void setUp() throws Exception
    {

        mockDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.executeUpdate()).thenReturn(1);

        dao = new MySqlProductDao(mockDataSource)
        {
            @Override
            protected Connection getConnection() throws SQLException
            {
                return mockConnection;
            }
        };
    }

    @Test
    void getById_shouldReturnProduct_whenProductExists() throws Exception
    {

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("product_id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Smartphone");
        when(mockResultSet.getBigDecimal("price")).thenReturn(new BigDecimal("499.99"));
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getString("description")).thenReturn("A phone");
        when(mockResultSet.getString("color")).thenReturn("Black");
        when(mockResultSet.getInt("stock")).thenReturn(20);
        when(mockResultSet.getBoolean("featured")).thenReturn(false);
        when(mockResultSet.getString("image_url")).thenReturn("phone.jpg");

        Product result = dao.getById(1);

        assertEquals(1, result.getProductId());
        assertEquals("Smartphone", result.getName());
    }

    @Test
    void search_shouldReturnOneProduct_whenResultSetHasOneRow() throws Exception
    {

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("product_id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Smartphone");
        when(mockResultSet.getBigDecimal("price")).thenReturn(new BigDecimal("499.99"));
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getString("description")).thenReturn("Nice phone");
        when(mockResultSet.getString("color")).thenReturn("Black");
        when(mockResultSet.getInt("stock")).thenReturn(20);
        when(mockResultSet.getBoolean("featured")).thenReturn(false);
        when(mockResultSet.getString("image_url")).thenReturn("phone.jpg");

        // Act
        List<Product> products = dao.search(1, new BigDecimal("100"), new BigDecimal("600"), "Black");

        // Assert
        assertEquals(1, products.size());
        assertEquals("Smartphone", products.get(0).getName());
    }

    @Test
    void update_shouldNotThrowException_whenValidProductGiven()
    {

        Product product = new Product();
        product.setName("Tablet");
        product.setPrice(new BigDecimal("199.99"));
        product.setCategoryId(2);
        product.setDescription("Updated tablet");
        product.setColor("Silver");
        product.setImageUrl("tablet.jpg");
        product.setStock(15);
        product.setFeatured(true);


        assertDoesNotThrow(() -> dao.update(1, product));
    }
}
