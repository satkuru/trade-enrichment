package com.scb.trade.enrichment.repository;

import com.scb.trade.enrichment.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class HashMapProductRepoImpl implements ProductRepo {
    private final String staticFile;
    private final Map<Integer,Product> cache = new ConcurrentHashMap<>();
    public HashMapProductRepoImpl(@Value("${enrich.product.static.file}") String staticFile){
        this.staticFile=staticFile;
        load(staticFile);
    }

    @Override
    public Product getById(int productId) {
        return cache.get(productId);
    }

    @Override
    public Collection<Product> getAll() {
        return cache.values();
    }
    void clearCache(){
     cache.clear();
    }

    void load(String staticFile){
        log.info("Loading Product from static file {}",staticFile);
        try {
            File productFile = ResourceUtils.getFile(staticFile);
            Map<Integer, Product> productMap = Files.lines(Path.of(productFile.getAbsolutePath()))
                .skip(1)//skip the header
                .map(line -> List.of(line.split(",")))
                .map(HashMapProductRepoImpl::toProduct)
                .collect(Collectors.toMap(Product::getId, Function.identity()));

            cache.putAll(productMap);
            log.info("Number of products loaded from the static file ={}",productMap.size());
        } catch (IOException e) {
            log.error("An exception was thrown while loading the product static file into cache",e);
            throw new RuntimeException(e);
        }
    }
    private static Product toProduct(List<String> col) {
        return Product.builder().id(Integer.valueOf(col.get(0))).name(col.get(1)).build();
    }
}
