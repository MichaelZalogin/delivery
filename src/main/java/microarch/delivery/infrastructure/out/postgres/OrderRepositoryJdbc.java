package microarch.delivery.infrastructure.out.postgres;

import microarch.delivery.application.OrderRepository;
import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import microarch.delivery.domain.model.order.Order;
import microarch.delivery.domain.model.order.OrderBuilder;
import microarch.delivery.domain.model.order.OrderStatus;
import org.jmolecules.ddd.annotation.Repository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;

import static libs.common.CommonUtils.mapIfNotNull;

@Repository
public class OrderRepositoryJdbc implements OrderRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderRepositoryJdbc(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private final RowMapper<Order> orderRowMapper = (rs, i) -> {
        Id orderId = Id.of(rs.getLong("id"));
        Location location = Location.create(rs.getInt("location_x"), rs.getInt("location_y"));

        return OrderBuilder.order()
                .id(orderId)
                .volume(rs.getInt("volume"))
                .location(location)
                .status(OrderStatus.valueOf(rs.getString("status")))
                .courierId(Id.ofNullable(rs.getObject("courier_id", Long.class)))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .createdBy(rs.getString("created_by"))
                .modifiedAt(rs.getObject("modified_at", OffsetDateTime.class))
                .modifiedBy(rs.getString("modified_by"))
                .version(rs.getLong("version"))
                .build();
    };

    @Override
    public Order save(Order order) {
        String sql = """
                insert into orders (id, volume, status, location_x, location_y, courier_id, created_at, created_by, modified_at, modified_by, version)
                values (:id, :volume, :status, :locationX, :locationY, :courierId, :createdAt, :createdBy,:modifiedAt, :modifiedBy, :version)
                on conflict (id) do update
                set
                    volume        = :volume,
                    status        = :status,
                    location_x    = :locationX,
                    location_y    = :locationY,
                    courier_id    = :courierId,
                    modified_at   = :modifiedAt,
                    modified_by   = :modifiedBy,
                    version       = :version
                where orders.version = :expectedVersion;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", order.getId().id(), Types.BIGINT)
                .addValue("volume", order.getVolume(), Types.INTEGER)
                .addValue("status", order.getStatus().name(), Types.VARCHAR)
                .addValue("locationX", order.getLocation().x(), Types.INTEGER)
                .addValue("locationY", order.getLocation().y(), Types.INTEGER)
                .addValue("courierId", mapIfNotNull(order.getCourierId(), Id::id), Types.BIGINT)
                .addValue("createdAt", order.getCreatedAt(), Types.TIMESTAMP_WITH_TIMEZONE)
                .addValue("createdBy", order.getCreatedBy(), Types.VARCHAR)
                .addValue("modifiedAt", order.getModifiedAt(), Types.TIMESTAMP_WITH_TIMEZONE)
                .addValue("modifiedBy", order.getModifiedBy(), Types.VARCHAR)
                .addValue("version", order.getVersion() + 1, Types.BIGINT)
                .addValue("expectedVersion", order.getVersion(), Types.BIGINT);

        int updated = namedParameterJdbcTemplate.update(sql, params);

        if (updated == 0) {
            throw new OptimisticLockingFailureException("Order " + order.getId() + " was modified concurrently");
        }

        return order;
    }

    @Override
    public Order findById(Id orderId) {
        String sql = """
                select id, volume, status, location_x, location_y, courier_id, created_at, created_by, modified_at, modified_by, version
                from orders
                where id = :orderId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("orderId", orderId.id());
        List<Order> result = namedParameterJdbcTemplate.query(sql, params, orderRowMapper);
        return DataAccessUtils.singleResult(result);
    }

    @Override
    public Order getNewCreatedOrder() {
        String sql = """
                select id, volume, status, location_x, location_y, courier_id, created_at, created_by, modified_at, modified_by, version
                from orders
                where status = 'CREATED'
                order by created_at desc
                limit 1;
                """;
        List<Order> result = namedParameterJdbcTemplate.query(sql, orderRowMapper);
        return DataAccessUtils.singleResult(result);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus orderStatus) {
        String sql = """
                select id, volume, status, location_x, location_y, courier_id, created_at, created_by, modified_at, modified_by, version
                from orders
                where status = :status;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("status", orderStatus.name());
        return namedParameterJdbcTemplate.query(sql, params, orderRowMapper);
    }
}
