package com.viaz.dashboard.features.shipment.specification;

import com.viaz.dashboard.features.carrier.Carrier;
import com.viaz.dashboard.features.shipment.Shipment;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShipmentSpecifications {

    public static Specification<Shipment> buildExcelSpec(
            List<String> statuses,
            List<String> clients,
            List<String> carriers,
            String idFrom,
            String idTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            if (clients != null && !clients.isEmpty()) {
                predicates.add(root.get("client").in(clients));
            }

            if (carriers != null && !carriers.isEmpty()) {
                predicates.add(root.get("carrier").in(carriers));
            }

            if (idFrom != null && !idFrom.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fullViazId"), idFrom));
            }

            if (idTo != null && !idTo.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fullViazId"), idTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Shipment> buildSpec(
            List<String> statuses,
            List<String> clients,
            List<String> carriers,
            String search
    ) {
        Specification<Shipment> spec = (root, query, cb) -> cb.conjunction(); // start with a no-op spec

        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(ShipmentSpecifications.hasStatuses(statuses));
        }

        if (clients != null && !clients.isEmpty()) {
            spec = spec.and(ShipmentSpecifications.hasClients(clients));
        }

        if (carriers != null && !carriers.isEmpty()) {
            spec = spec.and(ShipmentSpecifications.hasCarrierNames(carriers));
        }

        if (search != null && !search.isBlank()) {
            spec = spec.and(ShipmentSpecifications.matchesSearch(search));
        }

        return spec;
    }


    public static Specification<Shipment> notDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }


    public static Specification<Shipment> hasStatuses(List<String> statuses) {
        return (root, query, builder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return builder.conjunction(); // returns a no-op condition
            }
            return root.get("status").in(statuses);
        };
    }

    public static Specification<Shipment> hasClients(List<String> clients) {
        return (root, query, builder) -> {
            if (clients == null || clients.isEmpty()) {
                return builder.conjunction();
            }
            return root.get("client").in(clients);
        };
    }


    public static Specification<Shipment> hasCarrierNames(List<String> carriers) {
        return (root, query, builder) -> {
            if (carriers == null || carriers.isEmpty()) {
                return builder.conjunction();
            }
            return builder.lower(root.join("transport").get("name")).in(
                    carriers.stream().map(String::toLowerCase).toList()
            );
        };
    }

    public static Specification<Shipment> matchesSearch(String search) {
        return (root, query, cb) -> {
            String likePattern = "%" + search.toLowerCase() + "%";

            // Join to Carrier entity from transport field
            Join<Shipment, Carrier> carrierJoin = root.join("transport", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("fullViazId")), likePattern),
                    cb.like(cb.lower(root.get("client")), likePattern),
                    cb.like(cb.lower(carrierJoin.get("name")), likePattern)
            );
        };
    }

}

