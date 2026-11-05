package com.trujillo.franchise_api.infrastructure.persistence;


import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.model.Product;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public final class FranchiseItemMapper {

    public static final String FRANCHISE_SK = "METADATA";
    public static final String BRANCH_PREFIX = "BRANCH#";
    public static final String PRODUCT_PREFIX = "PRODUCT#";

    private FranchiseItemMapper() {
    }

    // ---------- Claves ----------

    public static String franchisePk(String franchiseId) {
        return "FRANCHISE#" + franchiseId;
    }

    public static String branchSk(String branchId) {
        return BRANCH_PREFIX + branchId;
    }

    public static String productSk(String branchId, String productId) {
        return PRODUCT_PREFIX + branchId + "#" + productId;
    }

    public static Map<String, AttributeValue> key(String pk, String sk) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("pk", str(pk));
        key.put("sk", str(sk));
        return key;
    }

    // ---------- Objeto de dominio -> ítem ----------

    public static Map<String, AttributeValue> toItem(Franchise franchise) {
        Map<String, AttributeValue> item = key(franchisePk(franchise.id()), FRANCHISE_SK);
        item.put("id", str(franchise.id()));
        item.put("name", str(franchise.name()));
        return item;
    }

    public static Map<String, AttributeValue> toItem(Branch branch) {
        Map<String, AttributeValue> item =
                key(franchisePk(branch.franchiseId()), branchSk(branch.id()));
        item.put("id", str(branch.id()));
        item.put("franchiseId", str(branch.franchiseId()));
        item.put("name", str(branch.name()));
        return item;
    }

    public static Map<String, AttributeValue> toItem(Product product) {
        Map<String, AttributeValue> item = key(
                franchisePk(product.franchiseId()),
                productSk(product.branchId(), product.id()));
        item.put("id", str(product.id()));
        item.put("franchiseId", str(product.franchiseId()));
        item.put("branchId", str(product.branchId()));
        item.put("name", str(product.name()));
        item.put("stock", num(product.stock()));
        return item;
    }

    // ---------- Ítem -> objeto de dominio ----------

    public static Franchise toFranchise(Map<String, AttributeValue> item) {
        return new Franchise(item.get("id").s(), item.get("name").s());
    }

    public static Branch toBranch(Map<String, AttributeValue> item) {
        return new Branch(
                item.get("id").s(),
                item.get("franchiseId").s(),
                item.get("name").s());
    }

    public static Product toProduct(Map<String, AttributeValue> item) {
        return new Product(
                item.get("id").s(),
                item.get("franchiseId").s(),
                item.get("branchId").s(),
                item.get("name").s(),
                Integer.parseInt(item.get("stock").n()));
    }

    // ---------- Ayudas para crear valores ----------

    public static AttributeValue str(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static AttributeValue num(int value) {
        return AttributeValue.builder().n(String.valueOf(value)).build();
    }
}

