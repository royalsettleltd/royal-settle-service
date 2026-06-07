package africa.royalsettle.thrift.repository;

import africa.royalsettle.thrift.models.ThriftContribution;
import africa.royalsettle.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ThriftContributionRepository extends JpaRepository<ThriftContribution, Long> {
    Optional<ThriftContribution> findByTransaction(Transaction transaction);
}
