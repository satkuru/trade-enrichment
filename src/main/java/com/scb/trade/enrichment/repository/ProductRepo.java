package com.scb.trade.enrichment.repository;

import com.scb.trade.enrichment.entity.Product;

import java.util.Collection;

public interface ProductRepo {
    Product getById(int productId);
    Collection<Product> getAll();
}
