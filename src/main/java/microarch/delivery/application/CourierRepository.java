package microarch.delivery.application;

import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.courier.Courier;
import org.jmolecules.ddd.types.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CourierRepository extends Repository<Courier, Id> {

    @Transactional(rollbackFor = Exception.class)
    Courier save(Courier courier);

    Courier findById(Id courierId);

    List<Courier> getFreeCouriers();
}