package com.ursa.tools.amazon.service;

import com.ursa.tools.amazon.model.Seller;

public interface SellerService {
    Seller findByName(String name);
    Seller save(Seller seller);
}
