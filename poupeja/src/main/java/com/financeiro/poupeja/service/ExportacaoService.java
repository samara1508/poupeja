package com.financeiro.poupeja.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;

import com.financeiro.poupeja.util.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExportacaoService {

    public void exportarPdf(InputStream resourceStream, Collection<?> dados, String caminhoDestino) throws Exception {
        if (Utils.isEmpty(resourceStream)) {
            throw new IllegalArgumentException("Template do relatório não pode ser nulo.");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(resourceStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);
        Map<String, Object> parameters = new HashMap<>();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, caminhoDestino);
    }

    public void exportarExcel(InputStream resourceStream, Collection<?> dados, String caminhoDestino) throws Exception {
        if (Utils.isEmpty(resourceStream)) {
            throw new IllegalArgumentException("Template do relatório não pode ser nulo.");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(resourceStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);
        Map<String, Object> parameters = new HashMap<>();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(caminhoDestino)));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(true);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setRemoveEmptySpaceBetweenColumns(true);
        configuration.setWhitePageBackground(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
    }
}
