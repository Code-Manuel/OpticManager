package com.opticpatientmanager.service;

import com.opticpatientmanager.model.OpticalPrescription;
import com.opticpatientmanager.model.Patient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float MARGIN = 50f;
    private static final float W = PDRectangle.A4.getWidth();   // 595.28
    private static final float H = PDRectangle.A4.getHeight();  // 841.89

    // Brand color: #2563EB
    private static final float[] BLUE   = {0.145f, 0.392f, 0.922f};
    // OD color: #0ea5e9
    private static final float[] SKY    = {0.055f, 0.647f, 0.914f};
    // OS color: #6366f1
    private static final float[] INDIGO = {0.388f, 0.400f, 0.945f};
    private static final float[] WHITE  = {1f, 1f, 1f};
    private static final float[] BLACK  = {0f, 0f, 0f};
    private static final float[] GRAY   = {0.45f, 0.45f, 0.45f};
    private static final float[] LGRAY  = {0.80f, 0.80f, 0.80f};
    private static final float[] AMBER  = {0.96f, 0.62f, 0.04f};
    private static final float[] CREAM  = {0.99f, 0.97f, 0.93f};

    public byte[] generatePrescriptionPdf(OpticalPrescription rx) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font bold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font oblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = H - MARGIN;

                // ── Header bar ─────────────────────────────────────────
                fillRect(cs, BLUE, MARGIN, y - 48, W - 2 * MARGIN, 58);

                text(cs, bold, 22, WHITE, MARGIN + 14, y - 26, "OpticManager");
                text(cs, regular, 9, new float[]{0.8f, 0.9f, 1f}, MARGIN + 14, y - 40,
                        "Gestionale per studio di ottica");

                // ── Title ──────────────────────────────────────────────
                y -= 72;
                text(cs, bold, 17, BLUE, MARGIN, y, "PRESCRIZIONE OTTICA");

                y -= 6;
                hline(cs, BLUE, 1.5f, MARGIN, y, W - 2 * MARGIN);

                y -= 20;

                // ── Patient info ───────────────────────────────────────
                Patient p = rx.getPatient();
                y = labelValue(cs, bold, regular, 13, y, "Paziente:",
                        safe(p.getFirstName()) + " " + safe(p.getLastName()));
                y = labelValue(cs, bold, regular, 13, y, "Codice Fiscale:", safe(p.getFiscalCode()));
                if (p.getBirthDate() != null) {
                    y = labelValue(cs, bold, regular, 13, y, "Data di nascita:", p.getBirthDate().format(FMT));
                }
                y = labelValue(cs, bold, regular, 13, y, "Data visita:", rx.getVisitDate().format(FMT));

                y -= 16;

                // ── OD ─────────────────────────────────────────────────
                fillRect(cs, SKY, MARGIN, y - 4, W - 2 * MARGIN, 22);
                text(cs, bold, 10, WHITE, MARGIN + 8, y + 4, "OCCHIO DESTRO (OD)  -  Oculus Dexter");
                y -= 12;
                y = eyeValues(cs, bold, regular, y, rx.getSphereOD(), rx.getCylinderOD(), rx.getAxisOD());

                y -= 14;

                // ── OS ─────────────────────────────────────────────────
                fillRect(cs, INDIGO, MARGIN, y - 4, W - 2 * MARGIN, 22);
                text(cs, bold, 10, WHITE, MARGIN + 8, y + 4, "OCCHIO SINISTRO (OS)  -  Oculus Sinister");
                y -= 12;
                y = eyeValues(cs, bold, regular, y, rx.getSphereOS(), rx.getCylinderOS(), rx.getAxisOS());

                y -= 14;

                // ── PD ─────────────────────────────────────────────────
                if (rx.getPupillaryDistance() != null) {
                    y = labelValue(cs, bold, regular, 13, y,
                            "Dist. interpupillare:", rx.getPupillaryDistance().toPlainString() + " mm");
                    y -= 4;
                }

                // ── Notes ──────────────────────────────────────────────
                if (rx.getNotes() != null && !rx.getNotes().isBlank()) {
                    y -= 10;
                    String noteText = truncate(safe(rx.getNotes()), 110);
                    boolean multiLine = noteText.length() > 60;
                    float noteH = multiLine ? 54 : 38;

                    fillRect(cs, CREAM, MARGIN, y - noteH + 14, W - 2 * MARGIN, noteH);
                    // amber left border
                    cs.setStrokingColor(AMBER[0], AMBER[1], AMBER[2]);
                    cs.setLineWidth(3.5f);
                    cs.moveTo(MARGIN, y + 14);
                    cs.lineTo(MARGIN, y - noteH + 14);
                    cs.stroke();

                    text(cs, bold, 10, new float[]{0.5f, 0.3f, 0f}, MARGIN + 10, y + 2, "Note:");
                    y -= 14;
                    text(cs, oblique, 10, new float[]{0.3f, 0.2f, 0f}, MARGIN + 10, y, noteText);
                    y -= noteH - 16;
                }

                // ── Signature block ────────────────────────────────────
                float footerTop = MARGIN + 75;
                hline(cs, LGRAY, 0.5f, MARGIN, footerTop, W - 2 * MARGIN);

                text(cs, bold, 10, GRAY, MARGIN, footerTop - 18, "Firma del professionista:");
                hline(cs, GRAY, 0.5f, MARGIN, footerTop - 35, 200);

                text(cs, bold, 10, GRAY, W / 2f, footerTop - 18, "Data:");
                text(cs, regular, 10, BLACK, W / 2f + 38, footerTop - 18, LocalDate.now().format(FMT));

                // ── Footer brand ───────────────────────────────────────
                hline(cs, LGRAY, 0.5f, MARGIN, MARGIN + 18, W - 2 * MARGIN);
                text(cs, regular, 7.5f, LGRAY, MARGIN, MARGIN + 7,
                        "Generato da OpticManager  -  " + LocalDate.now().format(FMT));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    // ── Drawing helpers ──────────────────────────────────────────────────

    private float labelValue(PDPageContentStream cs, PDType1Font bold, PDType1Font regular,
                              float size, float y, String label, String value) throws IOException {
        float lw = bold.getStringWidth(label) / 1000f * size;
        text(cs, bold, size, GRAY, MARGIN, y, label);
        text(cs, regular, size, BLACK, MARGIN + lw + 6, y, value);
        return y - (size + 7);
    }

    private float eyeValues(PDPageContentStream cs, PDType1Font bold, PDType1Font regular,
                             float y, BigDecimal sphere, BigDecimal cylinder, Integer axis) throws IOException {
        float col1 = MARGIN + 12;
        float col2 = MARGIN + 140;
        float col3 = MARGIN + 285;

        // Sub-labels
        y -= 8;
        text(cs, bold, 8.5f, GRAY, col1, y, "SFERA (Sph)");
        text(cs, bold, 8.5f, GRAY, col2, y, "CILINDRO (Cyl)");
        text(cs, bold, 8.5f, GRAY, col3, y, "ASSE (Ax)");

        y -= 18;
        // Values in larger font
        text(cs, bold, 16, BLACK, col1, y, fmtVal(sphere));
        text(cs, bold, 16, BLACK, col2, y, fmtVal(cylinder));
        text(cs, regular, 16, BLACK, col3, y, axis != null ? axis + "\u00b0" : "---");

        return y - 22;
    }

    private void text(PDPageContentStream cs, PDType1Font font, float size,
                      float[] rgb, float x, float y, String txt) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
        cs.newLineAtOffset(x, y);
        cs.showText(txt);
        cs.endText();
    }

    private void fillRect(PDPageContentStream cs, float[] rgb,
                           float x, float yBottom, float w, float h) throws IOException {
        cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
        cs.addRect(x, yBottom, w, h);
        cs.fill();
    }

    private void hline(PDPageContentStream cs, float[] rgb, float lw,
                        float x, float y, float len) throws IOException {
        cs.setStrokingColor(rgb[0], rgb[1], rgb[2]);
        cs.setLineWidth(lw);
        cs.moveTo(x, y);
        cs.lineTo(x + len, y);
        cs.stroke();
    }

    private String fmtVal(BigDecimal v) {
        if (v == null) return "---";
        double d = v.doubleValue();
        return (d >= 0 ? "+" : "") + String.format("%.2f", d);
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("\u00e0", "a'").replace("\u00e8", "e'").replace("\u00e9", "e'")
                .replace("\u00ec", "i'").replace("\u00f2", "o'").replace("\u00f9", "u'")
                .replace("\u00c0", "A'").replace("\u00c8", "E'").replace("\u00c9", "E'")
                .replace("\u00cc", "I'").replace("\u00d2", "O'").replace("\u00d9", "U'");
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "..." : (s != null ? s : "");
    }
}
