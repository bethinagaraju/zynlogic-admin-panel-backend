package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    List<LoginLog> findByUsernameOrderByLoginTimeDesc(String username);

    List<LoginLog> findBySuccessOrderByLoginTimeDesc(boolean success);

    @Query("SELECT l FROM LoginLog l WHERE l.loginTime BETWEEN :startDate AND :endDate ORDER BY l.loginTime DESC")
    List<LoginLog> findByLoginTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<LoginLog> findAllByOrderByLoginTimeDesc();
}