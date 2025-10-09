package com.viaz.dashboard.features.rating;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {


    @Query(value = """
            SELECT 
                r.carrier AS carrier_id,
                TO_CHAR(r.created_date, 'Mon') AS month,
                DATE_PART('month', r.created_date) AS month_number,
                ROUND(AVG(r.shipment_rating)::numeric, 2) AS avg_shipment_rating,
                ROUND(AVG(r.customer_service_rating)::numeric, 2) AS avg_customer_service_rating
            FROM ratings r
            WHERE r.created_date >= now() - interval '6 months'
            GROUP BY r.carrier, TO_CHAR(r.created_date, 'Mon'), DATE_PART('month', r.created_date)
            ORDER BY r.carrier, month_number
            """, nativeQuery = true)
    List<Object[]> findMonthlyAverageRatingsForAllCarriers();


    @Query(value = """
            SELECT
                r.carrier AS carrier_id,
                TO_CHAR(r.created_date, 'Mon') AS month,
                DATE_PART('month', r.created_date) AS month_number,
                r.shipment_rating,
                r.customer_service_rating
            FROM ratings r
            WHERE r.created_date >= now() - interval '6 months'
              AND r.carrier = :carrierId
            ORDER BY month_number
            """, nativeQuery = true)
    List<Object[]> findRatingsForCarrier(@Param("carrierId") Long carrierId);


    //    CHANGE DATE FOR TESTING PURPOSES ONLY
    @Modifying
    @Transactional
    @Query("UPDATE Rating r SET r.createdDate = :createdDate WHERE r.ratingId = :id")
    void updateCreatedDate(@Param("id") Long id, @Param("createdDate") Instant createdDate);


}
