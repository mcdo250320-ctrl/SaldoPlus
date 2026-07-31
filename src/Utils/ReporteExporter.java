package Utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

import javax.swing.JTable;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReporteExporter {

    // Exportar a Excel (CSV con UTF-8 tomando solo datos FILTRADOS)
    public static boolean exportarExcel(JTable tabla, File archivo) {
        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".csv") && !ruta.toLowerCase().endsWith(".xlsx")) {
            archivo = new File(ruta + ".csv");
        } else if (ruta.toLowerCase().endsWith(".xlsx")) {
            archivo = new File(ruta.substring(0, ruta.length() - 5) + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write('\ufeff'); // BOM para acentos en Excel

            // Encabezados
            int colCount = tabla.getColumnCount();
            for (int col = 0; col < colCount; col++) {
                writer.print("\"" + tabla.getColumnName(col) + "\"");
                if (col < colCount - 1) writer.print(",");
            }
            writer.println();

            // Filas visibles/filtradas
            int rowCount = tabla.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Object val = tabla.getValueAt(row, col);
                    String texto = val != null ? val.toString().replace("\"", "\"\"") : "";
                    writer.print("\"" + texto + "\"");
                    if (col < colCount - 1) writer.print(",");
                }
                writer.println();
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error exportando a Excel: " + e.getMessage());
            return false;
        }
    }

    // Exportar a PDF con Gráficas, KPIs y soporte para datos FILTRADOS
    public static boolean exportarPDF(JTable tabla, File archivo) {
        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            archivo = new File(ruta + ".pdf");
        }

        // Usamos la JTable directamente para respetar filtros de búsqueda/sorter
        int rowCount = tabla.getRowCount();
        int colCount = tabla.getColumnCount();

        // 1. Identificar columnas "Tipo" y "Monto"
        int colTipoIdx = -1;
        int colMontoIdx = -1;

        for (int c = 0; c < colCount; c++) {
            String colName = tabla.getColumnName(c).trim().toLowerCase();
            if (colName.contains("tipo")) colTipoIdx = c;
            if (colName.contains("monto") || colName.contains("cantidad")) colMontoIdx = c;
        }

        // 2. Calcular Métricas Financieras sobre los datos filtrados
        double totalIngresos = 0.0;
        double totalEgresos = 0.0;
        int numIngresos = 0;
        int numEgresos = 0;

        for (int row = 0; row < rowCount; row++) {
            String tipoVal = "";
            double montoVal = 0.0;

            if (colTipoIdx != -1 && tabla.getValueAt(row, colTipoIdx) != null) {
                tipoVal = tabla.getValueAt(row, colTipoIdx).toString().trim().toLowerCase();
            }

            if (colMontoIdx != -1 && tabla.getValueAt(row, colMontoIdx) != null) {
                try {
                    String strMonto = tabla.getValueAt(row, colMontoIdx).toString().replaceAll("[^0-9.-]", "");
                    montoVal = Double.parseDouble(strMonto);
                } catch (Exception ignored) {}
            }

            if (tipoVal.contains("egreso") || tipoVal.contains("gasto") || montoVal < 0) {
                totalEgresos += Math.abs(montoVal);
                numEgresos++;
            } else {
                totalIngresos += Math.abs(montoVal);
                numIngresos++;
            }
        }

        double balanceNeto = totalIngresos - totalEgresos;
        double tasaRetencion = totalIngresos > 0 ? (balanceNeto / totalIngresos) * 100 : 0;
        double ticketPromedio = rowCount > 0 ? (totalIngresos + totalEgresos) / rowCount : 0;

        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // --- HEADER ---
            content.setNonStrokingColor(25, 118, 210); // Azul ejecutivo
            content.addRect(40, 720, 532, 42);
            content.fill();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 15);
            content.setNonStrokingColor(Color.WHITE);
            content.newLineAtOffset(55, 734);
            content.showText("REPORTE FINANCIERO DE TRANSACCIONES");
            content.endText();

            // Metadatos
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
            content.setNonStrokingColor(100, 100, 100);
            content.newLineAtOffset(40, 705);
            content.showText("Emisión: " + sdf.format(new Date()) + " | Registros analizados: " + rowCount);
            content.endText();

            // --- TARJETAS KPI (Ingresos, Egresos, Balance) ---
            int boxY = 645;
            int boxWidth = 170;
            int boxHeight = 48;

            // Ingresos
            dibujarCajaMétrica(content, 40, boxY, boxWidth, boxHeight, "TOTAL INGRESOS (+)", formatoMoneda.format(totalIngresos), new Color(46, 125, 50), new Color(235, 247, 238));
            // Egresos
            dibujarCajaMétrica(content, 221, boxY, boxWidth, boxHeight, "TOTAL EGRESOS (-)", formatoMoneda.format(totalEgresos), new Color(198, 40, 40), new Color(253, 237, 237));
            // Balance
            dibujarCajaMétrica(content, 402, boxY, boxWidth, boxHeight, "BALANCE NETO", formatoMoneda.format(balanceNeto), new Color(21, 101, 192), new Color(238, 242, 250));

            // --- SECCIÓN: GRÁFICA & METRICAS SECUNDARIAS ---
            // Generar Gráfica de Pastel con JFreeChart
            BufferedImage pieChartImg = generarGraficoPastel(totalIngresos, totalEgresos);
            if (pieChartImg != null) {
                PDImageXObject pdImage = LosslessFactory.createFromImage(document, pieChartImg);
                content.drawImage(pdImage, 40, 490, 260, 145);
            }

            // Panel de Indicadores Secundarios (KPIs) a la derecha del gráfico
            int panelX = 310;
            int panelY = 490;
            content.setNonStrokingColor(248, 249, 250);
            content.addRect(panelX, panelY, 262, 145);
            content.fill();
            content.setStrokingColor(220, 224, 230);
            content.addRect(panelX, panelY, 262, 145);
            content.stroke();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.setNonStrokingColor(33, 33, 33);
            content.newLineAtOffset(panelX + 12, panelY + 125);
            content.showText("RESUMEN OPERATIVO");

            content.setFont(PDType1Font.HELVETICA, 9);
            content.newLineAtOffset(0, -22);
            content.showText("• Mov. de Ingreso: " + numIngresos + " transacciones");
            content.newLineAtOffset(0, -18);
            content.showText("• Mov. de Egreso: " + numEgresos + " transacciones");
            content.newLineAtOffset(0, -18);
            content.showText("• Promedio / Transacción: " + formatoMoneda.format(ticketPromedio));
            content.newLineAtOffset(0, -18);
            content.setFont(PDType1Font.HELVETICA_BOLD, 9);
            content.showText("• Retención de Capital: " + String.format("%.1f%%", tasaRetencion));
            content.endText();

            // --- TABLA DE TRANSACCIONES ---
            int startX = 40;
            int startY = 465;
            int rowHeight = 20;
            int tableWidth = 532;
            int colWidth = tableWidth / Math.max(1, colCount);

            // Cabecera Tabla
            content.setNonStrokingColor(230, 235, 245);
            content.addRect(startX, startY - rowHeight, tableWidth, rowHeight);
            content.fill();

            content.setFont(PDType1Font.HELVETICA_BOLD, 9);
            content.setNonStrokingColor(Color.BLACK);

            for (int col = 0; col < colCount; col++) {
                content.beginText();
                content.newLineAtOffset(startX + (col * colWidth) + 5, startY - 14);
                content.showText(tabla.getColumnName(col));
                content.endText();
            }

            int currentY = startY - rowHeight;
            content.setFont(PDType1Font.HELVETICA, 8);

            for (int row = 0; row < rowCount; row++) {
                currentY -= rowHeight;

                // Salto de Página Automático
                if (currentY < 45) {
                    content.close();
                    page = new PDPage();
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    currentY = 720;

                    // Re-dibujar encabezados en nueva página
                    content.setNonStrokingColor(230, 235, 245);
                    content.addRect(startX, currentY, tableWidth, rowHeight);
                    content.fill();

                    content.setFont(PDType1Font.HELVETICA_BOLD, 9);
                    content.setNonStrokingColor(Color.BLACK);
                    for (int col = 0; col < colCount; col++) {
                        content.beginText();
                        content.newLineAtOffset(startX + (col * colWidth) + 5, currentY + 5);
                        content.showText(tabla.getColumnName(col));
                        content.endText();
                    }
                    currentY -= rowHeight;
                    content.setFont(PDType1Font.HELVETICA, 8);
                }

                // Cebra alternada
                if (row % 2 == 0) {
                    content.setNonStrokingColor(250, 250, 250);
                    content.addRect(startX, currentY, tableWidth, rowHeight);
                    content.fill();
                }

                content.setNonStrokingColor(Color.DARK_GRAY);
                for (int col = 0; col < colCount; col++) {
                    Object val = tabla.getValueAt(row, col);
                    String texto = val != null ? val.toString().replaceAll("\r?\n", " ") : "";

                    if (texto.length() > 22) {
                        texto = texto.substring(0, 19) + "...";
                    }

                    content.beginText();
                    content.newLineAtOffset(startX + (col * colWidth) + 5, currentY + 5);
                    content.showText(texto);
                    content.endText();
                }

                content.setStrokingColor(230, 230, 230);
                content.moveTo(startX, currentY);
                content.lineTo(startX + tableWidth, currentY);
                content.stroke();
            }

            content.close();
            document.save(archivo);
            return true;
        } catch (Exception e) {
            System.err.println("Error generando reporte PDF profesional: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Auxiliar para dibujar tarjetas KPI
    private static void dibujarCajaMétrica(PDPageContentStream content, int x, int y, int w, int h, String titulo, String valor, Color colorTexto, Color colorFondo) throws Exception {
        content.setNonStrokingColor(colorFondo);
        content.addRect(x, y, w, h);
        content.fill();
        content.setStrokingColor(colorTexto);
        content.addRect(x, y, w, h);
        content.stroke();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 8);
        content.setNonStrokingColor(colorTexto);
        content.newLineAtOffset(x + 10, y + 30);
        content.showText(titulo);
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.newLineAtOffset(0, -16);
        content.showText(valor);
        content.endText();
    }

    // Auxiliar para construir la imagen del gráfico de pastel
    private static BufferedImage generarGraficoPastel(double ingresos, double egresos) {
        try {
            org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();
            dataset.setValue("Ingresos", ingresos);
            dataset.setValue("Egresos", egresos);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Distribución del Flujo",
                    dataset,
                    true,
                    false,
                    false
            );

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setSectionPaint("Ingresos", new Color(46, 125, 50));
            plot.setSectionPaint("Egresos", new Color(198, 40, 40));
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelGenerator(null); // Diseño limpio sin etiquetas de mapa flotantes

            return chart.createBufferedImage(500, 280);
        } catch (Exception e) {
            System.err.println("Error al renderizar gráfico JFreeChart: " + e.getMessage());
            return null;
        }
    }
}