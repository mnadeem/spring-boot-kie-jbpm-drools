package com.github.mnadeem.discount;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.mnadeem.discount.model.Sale;

@RestController
public class DiscountController {

	private DiscountService discountService;

	public DiscountController(DiscountService service) {
		this.discountService = service;
	}

	@PostMapping("/discount")
	private Sale getDiscountPercent(@RequestBody Sale sale) {		
		this.discountService.applyDiscount(sale);
	    return sale;
	}
}
