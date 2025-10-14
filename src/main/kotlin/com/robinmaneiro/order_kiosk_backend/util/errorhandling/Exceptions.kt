package com.robinmaneiro.order_kiosk_backend.util.errorhandling

class ProductNotFoundException(productId: String) : RuntimeException("Product $productId not found")
class InvalidQuantityException(qty: Int) : RuntimeException("Invalid quantity: $qty")
