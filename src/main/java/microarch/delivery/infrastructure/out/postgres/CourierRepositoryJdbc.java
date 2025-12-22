package microarch.delivery.infrastructure.out.postgres;

import libs.common.Timex;
import microarch.delivery.application.CourierRepository;
import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import microarch.delivery.domain.model.courier.*;
import org.jmolecules.ddd.annotation.Repository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;

import static libs.common.CommonUtils.mapIfNotNull;

@Repository
public class CourierRepositoryJdbc implements CourierRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CourierRepositoryJdbc(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private final RowMapper<Courier> courierRowMapper = (rs, i) -> {
        Id courierId = Id.of(rs.getLong("id"));
        Location location = Location.create(rs.getInt("location_x"), rs.getInt("location_y"));
        List<StoragePlace> storagePlaces = getStoragePlaceByCourierId(courierId);

        return CourierBuilder.courier()
                .id(courierId)
                .name(rs.getString("name"))
                .speed(rs.getInt("speed"))
                .location(location)
                .storagePlaces(storagePlaces)
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .createdBy(rs.getString("created_by"))
                .modifiedAt(rs.getObject("modified_at", OffsetDateTime.class))
                .modifiedBy(rs.getString("modified_by"))
                .version(rs.getLong("version"))
                .build();
    };

    private final RowMapper<StoragePlace> storagePlaceMapper = (rs, i) -> StoragePlaceBuilder.storagePlace()
            .id(Id.of(rs.getLong("id")))
            .placeType(StoragePlaceType.valueOf(rs.getString("place_type")))
            .orderId(Id.ofNullable(rs.getObject("order_id", Long.class)))
            .build();

    @Override
    public Courier save(Courier courier) {
        Id courierId = courier.getId();
        String sql = """
                insert into courier (id, name, speed, location_x, location_y, created_at, created_by, modified_at, modified_by, version)
                values (:id, :name, :speed, :locationX, :locationY, :createdAt, :createdBy, :modifiedAt, :modifiedBy, :version)
                on conflict (id) do update
                set name            = :name,
                    speed           = :speed,
                    location_x      = :locationX,
                    location_y      = :locationY,
                    modified_at     = :modifiedAt,
                    modified_by     = :modifiedBy,
                    version         = :version
                where courier.version = :expectedVersion;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", courierId.id(), Types.BIGINT)
                .addValue("name", courier.getName(), Types.VARCHAR)
                .addValue("speed", courier.getSpeed(), Types.INTEGER)
                .addValue("locationX", courier.getLocation().x(), Types.INTEGER)
                .addValue("locationY", courier.getLocation().y(), Types.INTEGER)
                .addValue("createdAt", courier.getCreatedAt(), Types.TIMESTAMP_WITH_TIMEZONE)
                .addValue("createdBy", courier.getCreatedBy(), Types.VARCHAR)
                .addValue("modifiedAt", courier.getModifiedAt(), Types.TIMESTAMP_WITH_TIMEZONE)
                .addValue("modifiedBy", courier.getModifiedBy(), Types.VARCHAR)
                .addValue("version", courier.getVersion() + 1, Types.BIGINT)
                .addValue("expectedVersion", courier.getVersion(), Types.BIGINT);
        int updated = namedParameterJdbcTemplate.update(sql, params);
        if (updated == 0) {
            throw new OptimisticLockingFailureException("Courier " + courierId + " was modified concurrently");
        }
        deleteStoragePlaces(courierId);
        saveStoragePlaces(courier.getStoragePlaces(), courierId);
        return courier;
    }

    @Override
    public Courier findById(Id courierId) {
        String sql = """
                select id, name, speed, location_x, location_y, created_at, created_by, modified_at, modified_by, version
                from courier
                where id = :courierId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("courierId", courierId.id());
        List<Courier> result = namedParameterJdbcTemplate.query(sql, params, courierRowMapper);
        return DataAccessUtils.singleResult(result);
    }

    @Override
    public List<Courier> getFreeCouriers() {
        String sql = """
                select cr.id, name, speed, location_x, location_y, created_at, created_by, modified_at, modified_by, version
                from courier cr
                left join storage_place sp on cr.id = sp.courier_id
                where sp.order_id is null
                """;
        return namedParameterJdbcTemplate.query(sql, courierRowMapper);
    }

    private void deleteStoragePlaces(Id courierId) {
        String sql = "delete from storage_place where courier_id = :courierId;";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue("courierId", courierId.id()));
    }

    private void saveStoragePlaces(List<StoragePlace> storagePlaces, Id courierId) {
        String sql = """
                insert into storage_place (id, courier_id, place_type, order_id)
                values (:id, :courierId, :placeType, :orderId);
                """;
        SqlParameterSource[] batchArgs = storagePlaces.stream().map(sp ->
                new MapSqlParameterSource()
                        .addValue("id", sp.getId().id(), Types.BIGINT)
                        .addValue("courierId", courierId.id(), Types.BIGINT)
                        .addValue("placeType", sp.getPlaceType().name(), Types.VARCHAR)
                        .addValue("orderId", mapIfNotNull(sp.getOrderId(), Id::id), Types.BIGINT)
        ).toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private List<StoragePlace> getStoragePlaceByCourierId(Id courierId) {
        String sql = """
                select id, courier_id, place_type, order_id
                from storage_place
                where courier_id = :courierId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("courierId", courierId.id());
        return namedParameterJdbcTemplate.query(sql, params, storagePlaceMapper);
    }
}
