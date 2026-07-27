package com.opticpatientmanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Rappresenta un documento PDF generato per una prescrizione ottica.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
    @SequenceGenerator(name = "document_seq", sequenceName = "document_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_document_prescription"))
    private OpticalPrescription prescription;

    /** Nome file del PDF */
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /** Contenuto binario del PDF */
    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    /** MIME type, di norma application/pdf */
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}
