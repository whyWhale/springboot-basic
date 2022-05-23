package com.programmers.order.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.javafaker.Faker;
import com.programmers.order.config.TestJdbcConfig;
import com.programmers.order.controller.dto.VoucherDto;
import com.programmers.order.domain.FixedVoucher;
import com.programmers.order.domain.PercentVoucher;
import com.programmers.order.domain.Voucher;
import com.programmers.order.domain.VoucherType;

@SpringJUnitConfig(TestJdbcConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VoucherJdbcRepositoryImplTest {

	@Autowired
	private VoucherRepository voucherRepository;
	private FixedVoucher fix;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private Faker faker = new Faker();

	@BeforeAll
	void init() {
		voucherRepository.deleteAll();
	}

	@Order(0)
	@Test
	void testDependency() {
		Assertions.assertThat(voucherRepository).isNotNull();
	}

	@Order(10)
	@Test
	void testInsert() {
		// given
		fix = FixedVoucher.builder()
				.voucherId(UUID.randomUUID())
				.voucherType(VoucherType.FIX)
				.discountValue(1000)
				.quantity(100)
				.expirationAt(LocalDateTime.now().plusDays(10))
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		// when
		Voucher insertedVoucher = voucherRepository.insert(fix);

		// then
		Assertions.assertThat(insertedVoucher).isNotNull();
		MatcherAssert.assertThat(insertedVoucher, Matchers.samePropertyValuesAs(fix));

	}

	@Order(20)
	@Test
	void testFindById() {
		// given

		// when
		Voucher foundVoucher = voucherRepository.findById(fix.getVoucherId()).orElseThrow(RuntimeException::new);
		// then
		Assertions.assertThat(foundVoucher).isNotNull();
		MatcherAssert.assertThat(fix, Matchers.samePropertyValuesAs(foundVoucher));
	}

	@Order(30)
	@Test
	void testUpdate() {
		// given
		FixedVoucher updatingVoucher = FixedVoucher.builder()
				.voucherId(fix.getVoucherId())
				.voucherType(VoucherType.FIX)
				.discountValue(1000)
				.quantity(100)
				.expirationAt(LocalDateTime.now().plusDays(4))
				.updatedAt(LocalDateTime.now())
				.build();

		// when
		Voucher update = voucherRepository.update(updatingVoucher);

		// then
		Assertions.assertThat(update).isNotNull();
		MatcherAssert.assertThat(update, Matchers.samePropertyValuesAs(updatingVoucher));
	}

	@Order(40)
	@Test
	void testDeleteByVoucherId() {
		voucherRepository.deleteByVoucherId(fix.getVoucherId());
	}

	@Order(50)
	@Test
	void testFindAll() {
		List<Voucher> fixedVouchers = makeVouchers();
		bulkInserts(fixedVouchers);

		VoucherDto.Search condition = VoucherDto.Search.builder()
				.build();

		Page<Voucher> pages = voucherRepository.findAll(PageRequest.of(0, 10), condition.buildCondition());

		List<Voucher> vouchers = pages.getContent();
		int totalPages = pages.getTotalPages();

		Assertions.assertThat(vouchers.size()).isEqualTo(10);
		Assertions.assertThat(totalPages).isEqualTo(12);
	}

	@Order(60)
	@DisplayName("fix voucher type 만 조회")
	@Test
	void testFindAllAndConditions() {

		VoucherDto.Search condition = VoucherDto.Search.builder()
				.type(VoucherType.FIX)
				.build();

		Page<Voucher> pages = voucherRepository.findAll(PageRequest.of(0, 10), condition.buildCondition());

		List<Voucher> vouchers = pages.getContent();
		int totalPages = pages.getTotalPages();

		Assertions.assertThat(vouchers.size()).isEqualTo(10);
		Assertions.assertThat(totalPages).isEqualTo(6);
	}

	public List<Voucher> makeVouchers() {

		List<Voucher> vouchers = new ArrayList<>();

		List<FixedVoucher> fixedVouchers = Stream.generate(() ->
						FixedVoucher.builder()
								.voucherId(UUID.randomUUID())
								.voucherType(VoucherType.FIX)
								.discountValue(faker.number().numberBetween(1, 100_000_000))
								.quantity(faker.number().numberBetween(1, 100_000_000))
								.expirationAt(LocalDateTime.now().plusDays(20))
								.createdAt(LocalDateTime.now())
								.updatedAt(LocalDateTime.now())
								.build()).limit(60)
				.toList();

		List<PercentVoucher> percentVouchers = Stream.generate(() ->
						PercentVoucher.builder()
								.voucherId(UUID.randomUUID())
								.voucherType(VoucherType.PERCENT)
								.discountValue(faker.number().numberBetween(1, 100))
								.quantity(faker.number().numberBetween(1, 100_000_000))
								.expirationAt(LocalDateTime.now().plusDays(20))
								.createdAt(LocalDateTime.now())
								.updatedAt(LocalDateTime.now())
								.build()).limit(60)
				.toList();

		vouchers.addAll(fixedVouchers);
		vouchers.addAll(percentVouchers);

		return vouchers;
	}

	public int[] bulkInserts(List<Voucher> vouchers) {

		return jdbcTemplate.batchUpdate("INSERT INTO vouchers VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?, ?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setBytes(1, vouchers.get(i).getVoucherId().toString().getBytes());
						ps.setString(2, vouchers.get(i).getVoucherType().toString());
						ps.setLong(3, vouchers.get(i).getDiscountValue());
						ps.setLong(4, vouchers.get(i).getQuantity());
						ps.setTimestamp(5, Timestamp.valueOf(vouchers.get(i).getExpirationAt()));
						ps.setTimestamp(6, Timestamp.valueOf(vouchers.get(i).getCreatedAt()));
						ps.setTimestamp(7, Timestamp.valueOf(vouchers.get(i).getUpdatedAt()));
					}

					@Override
					public int getBatchSize() {
						return 120;
					}
				});
	}
}