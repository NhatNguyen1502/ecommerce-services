package rookies.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class EcommerceApplication {

  public static void main(String[] args) {

    SpringApplication.run(EcommerceApplication.class, args);

    log.info("See Swagger UI at http://localhost:8080/swagger-ui.html");
  }
}
