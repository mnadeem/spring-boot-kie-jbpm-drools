package com.github.mnadeem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.mnadeem.discount.DiscountService;
import com.github.mnadeem.discount.model.Sale;

@SpringBootTest
class BootDroolsEmbeddedApplicationTests {
	
	@Autowired
	private DiscountService discountService;

	@Test
	void discountOnCarWithQuantityMoreThan5ShouldBe10() {
		Sale sale = new Sale();
		sale.setItem("Car");
		sale.setQuantity(11);
		discountService.applyDiscount(sale);
		
		assertEquals(10, sale.getDiscount());
	}
	
	@Test
	void discountOnBikeWithQuantityMoreThan10ShouldBe15() {
		Sale sale = new Sale();
		sale.setItem("Bike");
		sale.setQuantity(11);
		discountService.applyDiscount(sale);
		
		assertEquals(15, sale.getDiscount());
	}
}
