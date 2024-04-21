package fam.sub.repository;

import fam.sub.model.ChargedBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Month;
import java.util.List;
import java.util.Set;

public interface ChargedBillRepository extends JpaRepository<ChargedBill, Long> {
    List<ChargedBill> findAllByMonthInAndYear(Set<Month> monthSet, int year);
}
