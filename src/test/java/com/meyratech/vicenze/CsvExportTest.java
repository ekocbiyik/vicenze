package com.meyratech.vicenze;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.ui.util.UIUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * ekocbiyik on 04.08.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvExportTest {

    @Autowired
    private IInvoiceService invoiceService;

    private File file;
    private Workbook workbook;
    private Sheet sheet;
    private int rowNr;
    private int colNr;
    private Row row;
    private Cell cell;
    private CellStyle boldStyle;

    private Cell firstCell;
    private Cell lastCell;

    @Test
    public void csvExportTest() throws IOException {
        List<String> columnHeaders = Arrays.asList(
                "Project", "Vendor", "Event Type", "Main Item",
                "Book", "Transaction", "Invoice Number", "Invoice Code",
                "Explanation", "Amount", "Unit Prize", "Total Amount",
                "Date", "Created By", "Creation Date"
        );

        List<Invoice> invoiceList = invoiceService.getLastInvoices(10);

        // init file
        initTempFile();

        // initialize excel
        createContent();

        // Header
        addNewRow();
        columnHeaders.forEach(header -> {
            buildCell(header);
            cell.setCellStyle(getBoldStyle());
        });

        // Rows
        invoiceList.forEach(invoice -> {
            addNewRow();
            buildCell(invoice.getProject().getProjectName());
            if (firstCell == null) firstCell = cell;

            buildCell(invoice.getVendor());
            buildCell(invoice.getEventType());
            buildCell(invoice.getMainItem());
            buildCell(invoice.getBook());
            buildCell(invoice.getTransaction());
            buildCell(invoice.getInvoiceNumber());
            buildCell(invoice.getInvoiceCode());
            buildCell(invoice.getExplanation());
            buildCell(invoice.getAmount());
            buildCell(invoice.getUnitPrice());
            buildCell(invoice.getTotalAmount());
            buildCell(UIUtils.formatDatetime(invoice.getDate()));
            buildCell(invoice.getCreatedBy().getFullName());
            buildCell(UIUtils.formatDatetime(invoice.getCreationDate()));
        });

        if (lastCell == null) lastCell = cell;

        //footer
        buildFooter(columnHeaders.size());

        // write
        writeToFile();
        assert true;
    }

    private void initTempFile() throws IOException {
        if (file == null || file.delete()) {
            file = File.createTempFile("enbiya_deneme", ".xls");
        }
    }

    private void createContent() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet();
        colNr = 0;
        rowNr = 0;
        row = null;
        cell = null;
        boldStyle = null;
    }

    private void addNewRow() {
        row = sheet.createRow(rowNr);
        rowNr++;
        colNr = 0;
    }

    private void addNewCell() {
        cell = row.createCell(colNr);
        colNr++;
    }

    private void buildCell(Object value) {
        addNewCell();
        if (value == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
            cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
        } else if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            cell.setCellValue(calendar.getTime());
            cell.setCellType(Cell.CELL_TYPE_STRING);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        } else {
            cell.setCellValue(value.toString());
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
    }

    private CellStyle getBoldStyle() {
        if (boldStyle == null) {
            Font bold = workbook.createFont();
            bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
            boldStyle = workbook.createCellStyle();
            boldStyle.setFont(bold);
        }
        return boldStyle;
    }

    private void buildFooter(int colSize) {
        for (int i = 0; i < colSize; i++) {
            sheet.autoSizeColumn(i);
        }
        sheet.setAutoFilter(new CellRangeAddress(firstCell.getRowIndex(), lastCell.getRowIndex(), firstCell.getColumnIndex(), lastCell.getColumnIndex()));
    }

    private void writeToFile() {
        try {
            workbook.write(new FileOutputStream(file));
            LoggerFactory.getLogger(this.getClass()).info(file.getPath());
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error("Error writing excel file", e);
        }
    }

}
