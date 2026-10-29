package com.loctran.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loctran.store.entities.Product;
import com.loctran.store.mappers.ProductMapper;
import com.loctran.store.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.show-sql=false",
        "logging.level.org.hibernate.stat=OFF",
        "logging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=OFF"
})
@Transactional(readOnly = true)
@EnabledIfSystemProperty(named = "catalog.benchmark", matches = "true")
class CatalogBenchmarkTest {
    @Autowired ProductRepository products;
    @Autowired ProductMapper mapper;
    @Autowired EntityManager entityManager;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Test
    void compareEquivalentCatalogFetches() throws Exception {
        assertEquals("ecommerce_resume_bench", entityManager.createNativeQuery("SELECT current_database()", String.class).getSingleResult());
        assertEquals(50000, products.count(), "Run benchmarks/seed.sql in the isolated database first");
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        List<Map<String, Object>> results = new ArrayList<>();
        Long expectedChecksum = null;
        for (int round = -3; round < 10; round++) {
            for (int slot = 0; slot < 2; slot++) {
                boolean graph = Math.floorMod(round + slot, 2) == 0;
                entityManager.clear();
                stats.clear();
                long started = System.nanoTime();
                List<Product> rows = graph ? products.findAllWithCategory() : products.findAll();
                long checksum = 0;
                for (Product product : rows) {
                    var dto = mapper.productToProductDTO(product);
                    checksum += dto.getId() + dto.getCategoryId() + dto.getName().hashCode()
                            + dto.getDescription().hashCode() + dto.getPrice().hashCode();
                }
                double millis = (System.nanoTime() - started) / 1_000_000.0;
                long queries = stats.getPrepareStatementCount();
                assertEquals(50000, rows.size());
                if (expectedChecksum == null) expectedChecksum = checksum;
                assertEquals(expectedChecksum.longValue(), checksum, "Both paths must return equivalent DTO data");
                if (graph) assertEquals(1, queries, "Category entity graph should fetch in one SQL statement");
                if (round >= 0) results.add(Map.of("round", round, "variant", graph ? "entity_graph" : "default_findAll",
                        "latency_ms", millis, "sql_statements", queries, "rows", rows.size(), "checksum", checksum));
            }
        }
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("description", "Local warm-cache repository + DTO comparison; not HTTP latency or a historical before/after deployment");
        report.put("java_version", System.getProperty("java.version"));
        report.put("postgres_version", entityManager.createNativeQuery("SHOW server_version", String.class).getSingleResult());
        report.put("warmup_pairs", 3);
        report.put("measured_pairs", 10);
        report.put("results", results);
        Path output = Path.of("target", "catalog-benchmark.json");
        Files.createDirectories(output.getParent());
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(output.toFile(), report);
    }
}
