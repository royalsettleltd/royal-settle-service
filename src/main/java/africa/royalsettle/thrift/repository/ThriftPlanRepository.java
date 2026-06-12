package africa.royalsettle.thrift.repository;

import africa.royalsettle.thrift.models.ThriftPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThriftPlanRepository extends JpaRepository<ThriftPlan, Long> {

    Optional<ThriftPlan> findByCode(String thriftCode);
}