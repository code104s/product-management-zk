package com.nothing.onsite.productmanagementzk.repository;

import com.nothing.onsite.productmanagementzk.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long> {
}