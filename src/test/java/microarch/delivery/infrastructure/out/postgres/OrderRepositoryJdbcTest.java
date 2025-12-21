package microarch.delivery.infrastructure.out.postgres;

import microarch.BasePostgresContainerTest;
import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.order.Order;
import microarch.delivery.domain.model.order.OrderStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import static microarch.TestHelper.fullOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryJdbcTest extends BasePostgresContainerTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private OrderRepositoryJdbc orderRepositoryJdbc;

    @BeforeEach
    void setUp() {
        orderRepositoryJdbc = new OrderRepositoryJdbc(jdbcTemplate.getDataSource());
    }

    @Test
    void should_SaveOrder() {
        Order order = fullOrder().courierId(null).build();


        orderRepositoryJdbc.save(order);


        RowMapper<Boolean> assertRowMapper = (rs, rowNum) -> {
            SoftAssertions soft = new SoftAssertions();
            soft.assertThat(rs.getLong("id")).isPositive();
            soft.assertThat(rs.getInt("volume")).isEqualTo(order.getVolume());
            soft.assertThat(rs.getInt("location_x")).isEqualTo(order.getLocation().x());
            soft.assertThat(rs.getInt("location_y")).isEqualTo(order.getLocation().y());
            soft.assertThat(rs.getString("status")).isEqualTo(order.getStatus().name());
            soft.assertThat(rs.getObject("created_at", OffsetDateTime.class)).isCloseTo(order.getCreatedAt(), within(1, ChronoUnit.SECONDS));
            soft.assertThat(rs.getString("created_by")).isEqualTo(order.getCreatedBy());
            soft.assertThat(rs.getObject("modified_at", OffsetDateTime.class)).isCloseTo(order.getModifiedAt(), within(1, ChronoUnit.SECONDS));
            soft.assertThat(rs.getString("modified_by")).isEqualTo(order.getModifiedBy());
            soft.assertThat(rs.getLong("version")).isEqualTo(order.getVersion() + 1);
            soft.assertAll();
            return true;
        };
        List<Boolean> logs = jdbcTemplate.query("SELECT * FROM orders WHERE id = ?",
                assertRowMapper, order.getId().id());
        assertThat(logs).containsExactly(true);
    }

    @Test
    void should_GetOrder() {
        Order order = fullOrder().courierId(null).build();
        orderRepositoryJdbc.save(order);


        Order foundOrder = orderRepositoryJdbc.findById(order.getId());


        assertThat(foundOrder)
                .usingRecursiveComparison()
                .ignoringFields("version")
                .withComparatorForType(Comparator.comparing(OffsetDateTime::toInstant), OffsetDateTime.class)
                .isEqualTo(order);
    }

    @Test
    void should_GetNewCreatedOrder() {
        Order oldNewOrder = fullOrder().courierId(null).build();
        Order newOrder = fullOrder()
                .id(Id.generate())
                .courierId(null)
                .createdAt(oldNewOrder.getCreatedAt().minusMinutes(2))
                .build();
        orderRepositoryJdbc.save(oldNewOrder);
        orderRepositoryJdbc.save(newOrder);


        Order foundOrder = orderRepositoryJdbc.getNewCreatedOrder();


        assertThat(foundOrder)
                .usingRecursiveComparison()
                .ignoringFields("version")
                .withComparatorForType(Comparator.comparing(OffsetDateTime::toInstant), OffsetDateTime.class)
                .isEqualTo(oldNewOrder);
    }

    @Test
    void should_GetOrderByStatus() {
        Order oldNewOrder = fullOrder()
                .courierId(null)
                .status(OrderStatus.CREATED)
                .build();
        Order newOrder = fullOrder()
                .id(Id.generate())
                .courierId(null)
                .status(OrderStatus.ASSIGNED)
                .createdAt(oldNewOrder.getCreatedAt().minusMinutes(2))
                .build();
        orderRepositoryJdbc.save(oldNewOrder);
        orderRepositoryJdbc.save(newOrder);


        List<Order> foundOrders = orderRepositoryJdbc.getOrdersByStatus(OrderStatus.ASSIGNED);


        assertThat(foundOrders.stream().findFirst().get())
                .usingRecursiveComparison()
                .ignoringFields("version")
                .withComparatorForType(Comparator.comparing(OffsetDateTime::toInstant), OffsetDateTime.class)
                .isEqualTo(newOrder);
    }
}