package Utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JTable;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporteExporter {

    private static final Color VERDE_SACRAMENTO = new Color(0, 51, 25);
    private static final Color VERDE_INGRESO = new Color(34, 139, 34);
    private static final Color ROJO_EGRESO = new Color(178, 34, 34);
    private static final Color GRIS_FONDO = new Color(245, 245, 245);

    public static boolean exportarExcel(JTable tabla, File archivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historial_Transacciones");

            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE FINANCIERO DE TRANSACCIONES - SALDO+");
            titleCell.setCellStyle(titleStyle);

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(2);
            for (int col = 0; col < tabla.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(tabla.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            double totalIngresos = 0;
            double totalEgresos = 0;

            for (int row = 0; row < tabla.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 3);
                for (int col = 0; col < tabla.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object value = tabla.getValueAt(row, col);
                    String valStr = value != null ? value.toString() : "";
                    cell.setCellValue(valStr);

                    if (tabla.getColumnName(col).equalsIgnoreCase("Tipo")) {
                        Object montoVal = tabla.getValueAt(row, getColumnIndex(tabla, "Monto"));
                        if (montoVal != null) {
                            double montoClean = parseMonto(montoVal.toString());
                            if (valStr.equalsIgnoreCase("Ingreso")) {
                                totalIngresos += montoClean;
                            } else if (valStr.equalsIgnoreCase("Egreso")) {
                                totalEgresos += montoClean;
                            }
                        }
                    }
                }
            }

            int lastRow = tabla.getRowCount() + 4;
            Row summaryRow1 = sheet.createRow(lastRow++);
            summaryRow1.createCell(0).setCellValue("TOTAL INGRESOS (+):");
            summaryRow1.createCell(1).setCellValue(String.format("$ %.2f", totalIngresos));

            Row summaryRow2 = sheet.createRow(lastRow++);
            summaryRow2.createCell(0).setCellValue("TOTAL EGRESOS (-):");
            summaryRow2.createCell(1).setCellValue(String.format("$ %.2f", totalEgresos));

            Row summaryRow3 = sheet.createRow(lastRow);
            summaryRow3.createCell(0).setCellValue("BALANCE NETO:");
            summaryRow3.createCell(1).setCellValue(String.format("$ %.2f", (totalIngresos - totalEgresos)));

            for (int col = 0; col < tabla.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                workbook.write(fos);
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error al exportar Excel: " + e.getMessage());
            return false;
        }
    }

    public static boolean exportarPDF(JTable tabla, File archivo) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            double totalIngresos = 0;
            double totalEgresos = 0;
            int countIngresos = 0;
            int countEgresos = 0;
            int totalRegistros = tabla.getRowCount();

            int colMontoIdx = getColumnIndex(tabla, "Monto");
            int colTipoIdx = getColumnIndex(tabla, "Tipo");

            for (int row = 0; row < totalRegistros; row++) {
                String tipo = (colTipoIdx != -1 && tabla.getValueAt(row, colTipoIdx) != null) ? tabla.getValueAt(row, colTipoIdx).toString() : "";
                String montoStr = (colMontoIdx != -1 && tabla.getValueAt(row, colMontoIdx) != null) ? tabla.getValueAt(row, colMontoIdx).toString() : "0";
                double monto = parseMonto(montoStr);

                if (tipo.equalsIgnoreCase("Ingreso")) {
                    totalIngresos += monto;
                    countIngresos++;
                } else if (tipo.equalsIgnoreCase("Egreso")) {
                    totalEgresos += monto;
                    countEgresos++;
                }
            }

            double balance = totalIngresos - totalEgresos;
            double promedio = totalRegistros > 0 ? (totalIngresos + totalEgresos) / totalRegistros : 0;
            double retencion = totalIngresos > 0 ? (balance / totalIngresos) * 100 : 0;

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                cs.setNonStrokingColor(VERDE_SACRAMENTO);
                cs.addRect(40, 720, 515, 45);
                cs.fill();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 15);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(55, 737);
                cs.showText("REPORTE FINANCIERO DE TRANSACCIONES - SALDO+");
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                cs.setNonStrokingColor(Color.DARK_GRAY);
                cs.newLineAtOffset(40, 705);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                cs.showText("Emision: " + sdf.format(new Date()) + " | Registros analizados: " + totalRegistros);
                cs.endText();

                drawCard(cs, 40, 640, 160, 50, "TOTAL INGRESOS (+)", String.format("$ %.2f", totalIngresos), VERDE_INGRESO, new Color(240, 248, 240));
                drawCard(cs, 215, 640, 160, 50, "TOTAL EGRESOS (-)", String.format("$ %.2f", totalEgresos), ROJO_EGRESO, new Color(253, 242, 242));
                drawCard(cs, 390, 640, 165, 50, "BALANCE NETO", String.format("$ %.2f", balance), VERDE_SACRAMENTO, new Color(240, 245, 242));

                BufferedImage chartImage = generarGraficoPastel(totalIngresos, totalEgresos);
                if (chartImage != null) {
                    PDImageXObject pdChart = LosslessFactory.createFromImage(document, chartImage);
                    cs.drawImage(pdChart, 40, 480, 250, 150);
                }

                cs.setNonStrokingColor(GRIS_FONDO);
                cs.addRect(300, 480, 255, 150);
                cs.fill();

                cs.setStrokingColor(new Color(220, 220, 220));
                cs.setLineWidth(1.0f);
                cs.addRect(300, 480, 255, 150);
                cs.stroke();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                cs.setNonStrokingColor(Color.BLACK);
                cs.newLineAtOffset(315, 608);
                cs.showText("RESUMEN OPERATIVO");
                cs.endText();

                cs.setFont(PDType1Font.HELVETICA, 9);
                int textY = 588;
                drawBulletText(cs, 315, textY, "Mov. de Ingreso: " + countIngresos + " transacciones");
                textY -= 18;
                drawBulletText(cs, 315, textY, "Mov. de Egreso: " + countEgresos + " transacciones");
                textY -= 18;
                drawBulletText(cs, 315, textY, String.format("Promedio / Transaccion: $ %.2f", promedio));
                textY -= 18;
                drawBulletText(cs, 315, textY, String.format("Retencion de Capital: %.1f%%", retencion));

                int numColumnas = tabla.getColumnCount();
                float[] xPos;
                int[] maxChars;

                if (numColumnas >= 6) {
                    xPos = new float[]{45, 110, 160, 240, 315, 415};
                    maxChars = new int[]{10, 8, 12, 12, 16, 20};
                } else {
                    xPos = new float[]{45, 120, 180, 270, 360};
                    maxChars = new int[]{12, 10, 15, 15, 25};
                }

                int tableY = 440;
                cs.setNonStrokingColor(new Color(230, 238, 233));
                cs.addRect(40, tableY, 515, 20);
                cs.fill();

                cs.setFont(PDType1Font.HELVETICA_BOLD, 8);
                cs.setNonStrokingColor(VERDE_SACRAMENTO);
                for (int col = 0; col < numColumnas && col < xPos.length; col++) {
                    cs.beginText();
                    cs.newLineAtOffset(xPos[col], tableY + 6);
                    cs.showText(tabla.getColumnName(col));
                    cs.endText();
                }

                tableY -= 18;
                cs.setFont(PDType1Font.HELVETICA, 8);

                for (int row = 0; row < totalRegistros; row++) {
                    if (tableY < 40) break;

                    if (row % 2 == 1) {
                        cs.setNonStrokingColor(new Color(248, 248, 248));
                        cs.addRect(40, tableY - 4, 515, 16);
                        cs.fill();
                    }

                    cs.setNonStrokingColor(Color.BLACK);
                    for (int col = 0; col < numColumnas && col < xPos.length; col++) {
                        Object val = tabla.getValueAt(row, col);
                        String txt = val != null ? val.toString().trim() : "";
                        int limit = maxChars[col];
                        if (txt.length() > limit) {
                            txt = txt.substring(0, Math.max(0, limit - 3)) + "...";
                        }

                        cs.beginText();
                        cs.newLineAtOffset(xPos[col], tableY);
                        cs.showText(txt);
                        cs.endText();
                    }

                    tableY -= 16;
                }
            }

            document.save(archivo);
            return true;
        } catch (Exception e) {
            System.out.println("Error al exportar PDF profesional: " + e.getMessage());
            return false;
        }
    }

    private static BufferedImage generarGraficoPastel(double ingresos, double egresos) {
        try {
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Ingresos", ingresos);
            dataset.setValue("Egresos", egresos);

            JFreeChart chart = ChartFactory.createPieChart("Distribución del Flujo", dataset, true, false, false);
            chart.setBackgroundPaint(Color.WHITE);

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setSectionPaint("Ingresos", VERDE_INGRESO);
            plot.setSectionPaint("Egresos", ROJO_EGRESO);
            plot.setLabelGenerator(null);

            return chart.createBufferedImage(250, 150);
        } catch (Exception e) {
            System.out.println("Error generando gráfico de pastel: " + e.getMessage());
            return null;
        }
    }

    private static void drawCard(PDPageContentStream cs, float x, float y, float width, float height, String title, String value, Color textColor, Color bgColor) throws Exception {
        cs.setNonStrokingColor(bgColor);
        cs.addRect(x, y, width, height);
        cs.fill();

        cs.setStrokingColor(textColor);
        cs.setLineWidth(1.0f);
        cs.addRect(x, y, width, height);
        cs.stroke();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 8);
        cs.setNonStrokingColor(textColor);
        cs.newLineAtOffset(x + 10, y + height - 16);
        cs.showText(title);
        cs.endText();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
        cs.setNonStrokingColor(textColor);
        cs.newLineAtOffset(x + 10, y + 12);
        cs.showText(value);
        cs.endText();
    }

    private static void drawBulletText(PDPageContentStream cs, float x, float y, String text) throws Exception {
        cs.beginText();
        cs.setNonStrokingColor(Color.DARK_GRAY);
        cs.newLineAtOffset(x, y);
        cs.showText("• " + text);
        cs.endText();
    }

    private static int getColumnIndex(JTable table, String colName) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equalsIgnoreCase(colName)) {
                return i;
            }
        }
        return -1;
    }

    private static double parseMonto(String montoStr) {
        try {
            String clean = montoStr.replace("$", "").replace(",", "").trim();
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return 0.0;
        }
    }
}