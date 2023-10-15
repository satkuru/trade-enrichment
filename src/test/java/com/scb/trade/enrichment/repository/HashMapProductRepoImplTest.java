package com.scb.trade.enrichment.repository;

import com.scb.trade.enrichment.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class HashMapProductRepoImplTest {
    HashMapProductRepoImpl repo;
    private String staticFile;

    @BeforeEach
    void setUp() {
        staticFile = "src/main/resources/product.csv";
        repo = new HashMapProductRepoImpl(staticFile);
    }

    @Test
    void getById() {
        int validProductId = 1;
        int invalidPProductId = 11;
        Product product = Product.builder().name("Treasury Bills Domestic").id(1).build();
        Product validProduct = repo.getById(validProductId);
        assertThat(validProduct).isNotNull();
        assertThat(validProduct).isEqualTo(product);

        Product missingProduct = repo.getById(invalidPProductId);
        assertThat(missingProduct).isNull();
    }

    @Test
    void load() {
        repo.clearCache();
        repo.load(staticFile);
        Collection<Product> products = repo.getAll();
        assertThat(products.size()).isEqualTo(10);
    }
}
