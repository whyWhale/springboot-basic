package org.prgrms.kdt.voucher;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgrms.kdt.customer.CustomerNamedJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcVoucherRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepository.class);

    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.voucher"}
    )
    static class Config {
        @Bean
        public DataSource dataSource() {
            var dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("root1234!")
                    .type(HikariDataSource.class)
                    .build();
            return dataSource;
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }
    }

    @Autowired
    JdbcVoucherRepository jdbcVoucherRepository;

    @Autowired
    DataSource dataSource;

    Voucher newVoucher;

    @BeforeEach
    void delete() {
        jdbcVoucherRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("바우처를 추가할 수 있다.")
    public void testInsert() {

        try {
            jdbcVoucherRepository.insert(newVoucher);
        } catch (BadSqlGrammarException e) {
            logger.error("Got BadSqlGrammerException error code -> {}", e.getSQLException().getErrorCode(), e);
        }

        var retrievedVoucher = jdbcVoucherRepository.findById(newVoucher.getVoucherId());
        assertThat(retrievedVoucher.isEmpty(), is(false));
        assertThat(retrievedVoucher.get(), samePropertyValuesAs(newVoucher));
    }
}