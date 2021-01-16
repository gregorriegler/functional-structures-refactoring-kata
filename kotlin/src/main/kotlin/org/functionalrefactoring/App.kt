package org.functionalrefactoring

import org.functionalrefactoring.models.*
import java.math.BigDecimal

object App {
    fun applyDiscount(cartId: CartId, storage: Storage<Cart>) {
        loadCart(cartId).let {
            when (it) {
                Cart.MissingCart -> null
                else -> applyDiscountOnCart(it)
            }
        }
    }


    private fun applyDiscountOnCart(cart: Cart): Cart? {
        return lookupDiscountRule(cart.customerId).let {
            when (it) {
                DiscountRule.NoDiscount -> null
                else -> updateAmount(cart, it.apply(cart))
            }
        }
    }

    private fun loadCart(id: CartId): Cart {
        if (id.value.contains("gold"))
            return Cart(id, CustomerId("gold-customer"), Amount(BigDecimal(100)))
        return if (id.value.contains("normal")) Cart(
            id,
            CustomerId("normal-customer"),
            Amount(BigDecimal(100))
        ) else Cart.MissingCart
    }

    private fun lookupDiscountRule(id: CustomerId): DiscountRule {
        return if (id.value.contains("gold")) DiscountRule({ cart -> half(cart) }) else DiscountRule.NoDiscount
    }

    private fun updateAmount(cart: Cart, discount: Amount): Cart {
        return Cart(cart.id, cart.customerId, Amount(cart.amount.value.subtract(discount.value)))
    }

    private fun save(cart: Cart, storage: Storage<Cart>) {
        storage.flush(cart)
    }

    private fun half(cart: Cart): Amount {
        return Amount(cart.amount.value.divide(BigDecimal(2)))
    }
}

