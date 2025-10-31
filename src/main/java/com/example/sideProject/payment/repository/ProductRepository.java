package com.example.sideProject.payment.repository;

import com.example.sideProject.payment.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
