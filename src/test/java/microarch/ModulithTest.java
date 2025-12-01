package microarch;

import com.tngtech.archunit.core.domain.JavaClass;
import jdk.jfr.Description;
import microarch.delivery.DeliveryApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ModulithTest {

    ApplicationModules modules = ApplicationModules
            .of(DeliveryApplication.class, JavaClass.Predicates.resideInAPackage("microarch.delivery.kernel"));

    @Test
    @Description("Фильтруем нарушения: разрешаем циклы только между модулями")
    void should_VerifyWithoutModuleCycleCheck() {
        Violations violations = modules.detectViolations();

        List<String> errorMessages = violations.getMessages().stream()
                .filter(violation -> !violation.matches("(?s).\\w+ ->\\s+Slice (\\w+).*"))
                .toList();

        assertThat(errorMessages).isEmpty();
    }

    @Test
    void should_PrintModules() {
        modules.forEach(System.out::println);
    }
}
