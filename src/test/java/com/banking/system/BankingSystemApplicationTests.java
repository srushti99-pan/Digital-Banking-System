package com.banking.system;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/dummy_db",
    "spring.datasource.username=root",
    "spring.datasource.password=",
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=none"
})
@Disabled("Skipping full integration context load in offline database build")
class BankingSystemApplicationTests {

    @Test
    void contextLoads() {
    }
}
