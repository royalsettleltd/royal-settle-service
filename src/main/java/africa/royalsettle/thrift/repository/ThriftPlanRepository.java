package africa.royalsettle.thrift.repository;

import africa.royalsettle.thrift.model.ThriftPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThriftPlanRepository extends JpaRepository<ThriftPlan, String> {

    List<ThriftPlan> findByUserId(String userId);

    List<ThriftPlan> findByIsCompleted(Boolean isCompleted);
}