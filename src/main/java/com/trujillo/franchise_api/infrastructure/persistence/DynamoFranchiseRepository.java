package com.trujillo.franchise_api.infrastructure.persistence;

import com.trujillo.franchise_api.domain.model.Branch;
import com.trujillo.franchise_api.domain.model.Franchise;
import com.trujillo.franchise_api.domain.model.Product;
import com.trujillo.franchise_api.domain.repository.FranchiseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

@Repository
public class DynamoFranchiseRepository implements FranchiseRepository {

    private final DynamoDbAsyncClient dynamoDb;
    private final String tableName;

    public DynamoFranchiseRepository(DynamoDbAsyncClient dynamoDb, @Value("${aws.dynamodb.table-name}") String tableName) {
        this.dynamoDb = dynamoDb;
        this.tableName = tableName;
    }

    // ---------- Franquicias ----------

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        return putItem(FranchiseItemMapper.toItem(franchise)).thenReturn(franchise);
    }

    @Override
    public Mono<Franchise> findFranchiseById(String franchiseId) {
        String pk = FranchiseItemMapper.franchisePk(franchiseId);
        return getItem(pk, FranchiseItemMapper.FRANCHISE_SK).map(FranchiseItemMapper::toFranchise);
    }

    // ---------- Sucursales ----------

    @Override
    public Mono<Branch> saveBranch(Branch branch) {
        return putItem(FranchiseItemMapper.toItem(branch)).thenReturn(branch);
    }

    @Override
    public Mono<Branch> findBranch(String franchiseId, String branchId) {
        String pk = FranchiseItemMapper.franchisePk(franchiseId);
        String sk = FranchiseItemMapper.branchSk(branchId);
        return getItem(pk, sk).map(FranchiseItemMapper::toBranch);
    }

    @Override
    public Flux<Branch> findBranchesByFranchise(String franchiseId) {
        return queryByPrefix(franchiseId, FranchiseItemMapper.BRANCH_PREFIX).map(FranchiseItemMapper::toBranch);
    }

    // ---------- Productos ----------

    @Override
    public Mono<Product> saveProduct(Product product) {
        return putItem(FranchiseItemMapper.toItem(product)).thenReturn(product);
    }

    @Override
    public Mono<Product> findProduct(String franchiseId, String branchId, String productId) {
        String pk = FranchiseItemMapper.franchisePk(franchiseId);
        String sk = FranchiseItemMapper.productSk(branchId, productId);
        return getItem(pk, sk).map(FranchiseItemMapper::toProduct);
    }

    @Override
    public Mono<Void> deleteProduct(String franchiseId, String branchId, String productId) {
        String pk = FranchiseItemMapper.franchisePk(franchiseId);
        String sk = FranchiseItemMapper.productSk(branchId, productId);

        DeleteItemRequest request = DeleteItemRequest.builder().tableName(tableName).key(FranchiseItemMapper.key(pk, sk)).build();

        return Mono.fromFuture(() -> dynamoDb.deleteItem(request)).then();
    }

    @Override
    public Flux<Product> findProductsByFranchise(String franchiseId) {
        return queryByPrefix(franchiseId, FranchiseItemMapper.PRODUCT_PREFIX).map(FranchiseItemMapper::toProduct);
    }

    // ---------- Operaciones comunes de DynamoDB ----------

    private Mono<Void> putItem(Map<String, AttributeValue> item) {
        PutItemRequest request = PutItemRequest.builder().tableName(tableName).item(item).build();

        return Mono.fromFuture(() -> dynamoDb.putItem(request)).then();
    }

    private Mono<Map<String, AttributeValue>> getItem(String pk, String sk) {
        GetItemRequest request = GetItemRequest.builder().tableName(tableName).key(FranchiseItemMapper.key(pk, sk)).build();

        return Mono.fromFuture(() -> dynamoDb.getItem(request)).filter(GetItemResponse::hasItem)   // si no hay ítem, queda Mono vacío
                .map(GetItemResponse::item);
    }

    private Flux<Map<String, AttributeValue>> queryByPrefix(String franchiseId, String prefix) {
        QueryRequest request = QueryRequest.builder().tableName(tableName).keyConditionExpression("pk = :pk AND begins_with(sk, :prefix)").expressionAttributeValues(Map.of(":pk", FranchiseItemMapper.str(FranchiseItemMapper.franchisePk(franchiseId)), ":prefix", FranchiseItemMapper.str(prefix))).build();

        // El paginator pide las páginas siguientes automáticamente (DynamoDB devuelve máx. 1 MB por consulta)
        return Flux.from(dynamoDb.queryPaginator(request)).flatMapIterable(QueryResponse::items);
    }
}
