package codehows.com.daehoint.repository;

import codehows.com.daehoint.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepo extends JpaRepository<Member, String> {

}
