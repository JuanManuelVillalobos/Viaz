package com.viaz.dashboard.documents;


import com.viaz.dashboard.shipment.Shipment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {
    @Id
    private UUID documentId;

    private String fileName;
    private String fileType;
    private byte[] data; // Store the document as a byte array

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @PrePersist
    public void generateId() {
        this.documentId = UUID.randomUUID();
    }
}