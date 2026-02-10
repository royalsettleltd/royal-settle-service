package africa.royalsettle.thrift.repository;

import africa.royalsettle.thrift.model.ThriftContribution;
import africa.royalsettle.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository

public interface ThriftContributionRepository extends JpaRepository<ThriftContribution, String> {
    Optional<ThriftContribution> findByTransaction(Transaction transaction);
    @Query("SELECT SUM(tc.amount) FROM ThriftContribution tc WHERE tc.thriftPlan.id = :thriftPlanId")
    BigDecimal sumByThriftPlanId(String thriftPlanId);
}
