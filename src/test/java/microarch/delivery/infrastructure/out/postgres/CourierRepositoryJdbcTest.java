package microarch.delivery.infrastructure.out.postgres;

import microarch.BasePostgresContainerTest;
import microarch.delivery.domain.model.courier.Courier;
import microarch.delivery.domain.model.courier.StoragePlace;
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

import static microarch.TestHelper.fullCourierBuilder;
import static microarch.TestHelper.fullStoragePlace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CourierRepositoryJdbcTest extends BasePostgresContainerTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private CourierRepositoryJdbc courierRepository;

    @BeforeEach
    void setUp() {
        courierRepository = new CourierRepositoryJdbc(jdbcTemplate.getDataSource());
    }

    @Test
    void should_SaveCourier() {
        List<StoragePlace> storagePlaces = List.of(fullStoragePlace().orderId(null).build());
        Courier courier = fullCourierBuilder()
                .storagePlaces(storagePlaces)
                .build();


        courierRepository.save(courier);


        RowMapper<Boolean> assertRowMapper = (rs, rowNum) -> {
            SoftAssertions soft = new SoftAssertions();
            soft.assertThat(rs.getLong("id")).isPositive();
            soft.assertThat(rs.getString("name")).isEqualTo(courier.getName());
            soft.assertThat(rs.getInt("speed")).isEqualTo(courier.getSpeed());
            soft.assertThat(rs.getInt("location_x")).isEqualTo(courier.getLocation().x());
            soft.assertThat(rs.getInt("location_y")).isEqualTo(courier.getLocation().y());
            soft.assertThat(rs.getObject("created_at", OffsetDateTime.class)).isCloseTo(courier.getCreatedAt(), within(1, ChronoUnit.SECONDS));
            soft.assertThat(rs.getString("created_by")).isEqualTo(courier.getCreatedBy());
            soft.assertThat(rs.getObject("modified_at", OffsetDateTime.class)).isCloseTo(courier.getModifiedAt(), within(1, ChronoUnit.SECONDS));
            soft.assertThat(rs.getString("modified_by")).isEqualTo(courier.getModifiedBy());
            soft.assertThat(rs.getLong("version")).isEqualTo(courier.getVersion() + 1);
            soft.assertAll();
            return true;
        };
        List<Boolean> logs = jdbcTemplate.query("SELECT * FROM courier WHERE id = ?",
                assertRowMapper, courier.getId().id());
        assertThat(logs).containsExactly(true);
    }

    @Test
    void should_GetCourier() {
        List<StoragePlace> storagePlaces = List.of(fullStoragePlace().orderId(null).build());
        Courier courier = fullCourierBuilder()
                .storagePlaces(storagePlaces)
                .build();
        courierRepository.save(courier);


        Courier foundCourier = courierRepository.findById(courier.getId());


        assertThat(foundCourier)
                .usingRecursiveComparison()
                .ignoringFields("version")
                .withComparatorForType(Comparator.comparing(OffsetDateTime::toInstant), OffsetDateTime.class)
                .isEqualTo(courier);
    }

    @Test
    void should_GetFreeCourier() {
        List<StoragePlace> storagePlaces = List.of(fullStoragePlace().orderId(null).build());
        Courier courier = fullCourierBuilder()
                .storagePlaces(storagePlaces)
                .build();
        courierRepository.save(courier);


        List<Courier> foundCouriers = courierRepository.getFreeCouriers();


        assertThat(foundCouriers.get(0).getId()).isEqualTo(courier.getId());
    }
}
