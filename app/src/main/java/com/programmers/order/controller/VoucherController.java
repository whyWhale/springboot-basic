package com.programmers.order.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.programmers.order.controller.dto.PageDto;
import com.programmers.order.controller.dto.VoucherDto;
import com.programmers.order.converter.VoucherConverter;
import com.programmers.order.domain.Voucher;
import com.programmers.order.service.VoucherService;

@RequestMapping("/voucher")
@Controller
public class VoucherController {
	private final VoucherConverter converter;
	private final VoucherService voucherService;

	public VoucherController(VoucherConverter voucherConverter, VoucherService voucherService) {
		this.converter = voucherConverter;
		this.voucherService = voucherService;
	}

	@GetMapping("")
	public ModelAndView getVouchers(@ModelAttribute VoucherDto.Search searchDto,
			@ModelAttribute PageDto.Request paging) {
		// bug : sort by query 에 안먹힘;;;
		Pageable pageable = paging.getPageable(Sort.by("created_at").descending());
		Page<Voucher> vouchers = voucherService.lookUpVouchers(searchDto, pageable);
		PageDto.Response<VoucherDto.Response, Voucher> voucherResponses = converter.domainToResponseDtos()
				.convert(vouchers);

		ModelAndView modelAndView = new ModelAndView("voucher-inventory");
		modelAndView.addObject("vouchers", voucherResponses);

		return modelAndView;
	}

	@GetMapping("/read/{voucher_id}")
	public ModelAndView getVoucher(@PathVariable("voucher_id") UUID voucherId) {
		Voucher foundVoucher = voucherService.lookUpVoucher(voucherId);

		VoucherDto.Response response = converter.domainToResponseDto().convert(foundVoucher);
		ModelAndView readVoucherView = new ModelAndView("voucher-read");
		readVoucherView.addObject("voucher", response);
		return readVoucherView;
	}

	@GetMapping("/register")
	public ModelAndView register() {
		return new ModelAndView("voucher-register");
	}

	@GetMapping("/update/{voucher_id}")
	public ModelAndView update(@PathVariable("voucher_id") UUID voucherId) {
		Voucher voucher = voucherService.lookUpVoucher(voucherId);
		VoucherDto.Response response = converter.domainToResponseDto().convert(voucher);
		ModelAndView voucherUpdateView = new ModelAndView("voucher-update");
		voucherUpdateView.addObject("voucher", response);
		return voucherUpdateView;
	}
}

