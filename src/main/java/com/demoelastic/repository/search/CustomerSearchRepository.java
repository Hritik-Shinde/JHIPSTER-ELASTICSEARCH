package com.demoelastic.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.demoelastic.domain.Customer;
import com.demoelastic.repository.CustomerRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Customer} entity.
 */
public interface CustomerSearchRepository extends ElasticsearchRepository<Customer, String>, CustomerSearchRepositoryInternal {}

interface CustomerSearchRepositoryInternal {
    Stream<Customer> search(String query);

    Stream<Customer> search(Query query);

    void index(Customer entity);
}

class CustomerSearchRepositoryInternalImpl implements CustomerSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CustomerRepository repository;

    CustomerSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CustomerRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Customer> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setMinScore(0.02f);
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Customer> search(Query query) {
        return elasticsearchTemplate.search(query, Customer.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Customer entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
