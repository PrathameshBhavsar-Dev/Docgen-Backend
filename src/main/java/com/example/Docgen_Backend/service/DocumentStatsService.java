package com.example.Docgen_Backend.service;

import com.example.Docgen_Backend.dto.AnalyticsResponse;
import com.example.Docgen_Backend.dto.DailyCount;
import com.example.Docgen_Backend.dto.DocumentStatsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class DocumentStatsService {

    // every one of these has a createdAt date
    private static final List<String> DATED = List.of(
            "OfferLetter", "AppointmentLetter", "ConfirmationLetter", "IncrementLetter",
            "InternshipLetter", "CompletionLetter", "ExperienceLetter", "RelievingLetter",
            "FullAndFinalLetter", "GenericDocument");

    // visibility: match this to your UserRepository.findAllVisibleToUser rule
    private static final String MAIN_VISIBLE =
            "(d.userProfile.createdByUserId = :uid or d.userProfile.source = 'FORM')";
    private static final String VENDOR_VISIBLE = "d.userProfile.vendorId is not null";

    @PersistenceContext(unitName = "main")
    private EntityManager mainEm;

    @PersistenceContext(unitName = "vendor")
    private EntityManager vendorEm;

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    public AnalyticsResponse analytics(String userId) {
        LocalDate today = LocalDate.now(IST);
        Acc acc = new Acc(today.getYear(), today.minusDays(6));

        load(mainEm, MAIN_VISIBLE, userId, acc);
        load(vendorEm, VENDOR_VISIBLE, null, acc);

        List<Long> monthly = Arrays.stream(acc.monthly).boxed().toList();
        List<DailyCount> last7 = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = acc.weekStart.plusDays(i);
            last7.add(new DailyCount(day.toString(), acc.daily.getOrDefault(day, 0L)));
        }
        return new AnalyticsResponse(acc.total, acc.byType, acc.byCompany, monthly, last7);
    }

    private void load(EntityManager em, String visible, String userId, Acc acc) {
        for (String entity : DATED) {
            String jpql = "select d.createdAt, d.userProfile.company from " + entity
                    + " d where " + visible;
            for (Object r : run(em, jpql, userId)) {
                Object[] row = (Object[]) r;
                acc.add(entity, toLocalDate(row[0]), row[1]);
            }
        }
        // salary slips have no createdAt: they count in type and company totals only
        for (Object r : run(em, "select d.userProfile.company from SalarySlip d where " + visible, userId)) {
            acc.add("SalarySlip", null, r);
        }
    }

    private List<?> run(EntityManager em, String jpql, String userId) {
        Query q = em.createQuery(jpql);
        if (userId != null) q.setParameter("uid", userId);
        return q.getResultList();
    }

    private static LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDate ld) return ld;
        if (o instanceof LocalDateTime ldt) return ldt.toLocalDate();
        if (o instanceof java.sql.Date sd) return sd.toLocalDate();
        if (o instanceof java.util.Date ud) return ud.toInstant().atZone(IST).toLocalDate();
        return null;
    }

    private static final class Acc {
        final int year;
        final LocalDate weekStart;
        long total;
        final Map<String, Long> byType = new LinkedHashMap<>();
        final Map<String, Long> byCompany = new HashMap<>();
        final long[] monthly = new long[12];
        final Map<LocalDate, Long> daily = new HashMap<>();

        Acc(int year, LocalDate weekStart) { this.year = year; this.weekStart = weekStart; }

        void add(String type, LocalDate created, Object company) {
            total++;
            byType.merge(type, 1L, Long::sum);
            byCompany.merge(company instanceof Enum<?> e ? e.name() : "Unknown", 1L, Long::sum);
            if (created != null) {
                if (created.getYear() == year) monthly[created.getMonthValue() - 1]++;
                if (!created.isBefore(weekStart)) daily.merge(created, 1L, Long::sum);
            }
        }
    }

    public DocumentStatsResponse forUser(String userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate thisStart = today.withDayOfMonth(1);
        LocalDate lastStart = thisStart.minusMonths(1);

        long[] total = new long[3];   // [all, thisMonth, lastMonth]
        accumulate(total, mainEm, MAIN_VISIBLE, userId, thisStart, lastStart);
        accumulate(total, vendorEm, VENDOR_VISIBLE, null, thisStart, lastStart);

        return new DocumentStatsResponse(total[0], total[1], total[2]);
    }

    private void accumulate(long[] total, EntityManager em, String visible,
                            String userId, LocalDate thisStart, LocalDate lastStart) {
        for (String entity : DATED) {
            String jpql = "select count(d), "
                    + "coalesce(sum(case when d.createdAt >= :thisStart then 1 else 0 end), 0), "
                    + "coalesce(sum(case when d.createdAt >= :lastStart and d.createdAt < :thisStart then 1 else 0 end), 0) "
                    + "from " + entity + " d where " + visible;
            Query q = em.createQuery(jpql)
                    .setParameter("thisStart", thisStart)
                    .setParameter("lastStart", lastStart);
            if (userId != null) q.setParameter("uid", userId);

            Object[] row = (Object[]) q.getSingleResult();
            for (int i = 0; i < 3; i++) total[i] += ((Number) row[i]).longValue();
        }

        // salary slips have no createdAt, so they only count toward the total
        Query slips = em.createQuery("select count(d) from SalarySlip d where " + visible);
        if (userId != null) slips.setParameter("uid", userId);
        total[0] += ((Number) slips.getSingleResult()).longValue();
    }
}