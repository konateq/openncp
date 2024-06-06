package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc;

import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.Kpi1_2Strategy;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.KpiIdentifier;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.persistence.model.Transaction;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.persistence.model.TransactionError;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportServiceTest.class);

    @TempDir
    File tempDir;

    private final ExportService exportService;

    private final TransactionService mockedTransactionService;

    public ExportServiceTest() {
        mockedTransactionService = Mockito.mock(TransactionService.class);
        exportService = new ExportService(mockedTransactionService, List.of(new Kpi1_2Strategy()));
    }

    @Test
    void export_cp_80_kpi_1_2() throws IOException {
        final ZoneId zone = ZoneId.of("Europe/Paris");
        final Instant now = LocalDateTime.of(2024, Month.JANUARY, 31, 11, 10, 9).atZone(zone).toInstant();
        final Instant yesterday = now.minus(Duration.ofDays(1));

        final Transaction transaction = Mockito.mock(Transaction.class);
        when(transaction.getHomeISO()).thenReturn("home iso");
        when(transaction.getStartTime()).thenReturn(yesterday);
        when(transaction.getSndISO()).thenReturn("Sender iso");
        when(transaction.getReceivingISO()).thenReturn("Receiving iso");
        when(transaction.getEndTime()).thenReturn(now);
        when(transaction.getServiceType()).thenReturn("PATIENT_IDENTIFICATION_QUERY");
        when(transaction.getSndMsgID()).thenReturn("sender msg id");
        when(transaction.getReceivingMsgID()).thenReturn("receiver msg id");
        final TransactionError transactionError = new TransactionError();
        transactionError.setErrorDescription("error description");
        when(transaction.getTransactionError()).thenReturn(transactionError);


        final List<Transaction> transactions = List.of(transaction);
        final Page<Transaction> transactionPage = new PageImpl<>(transactions);
        when(mockedTransactionService.findTransactions(Pageable.unpaged())).thenReturn(transactionPage);

        final byte[] export = exportService.export(ZonedDateTime.ofInstant(now, zone).minusDays(5).toLocalDate(), ZonedDateTime.ofInstant(now, zone).plusDays(1).toLocalDate());
        assertThat(export).isNotEmpty();

        final File exportedCsv = new File(tempDir, RandomStringUtils.randomAlphabetic(5) + "_export.xlsx");
        Files.write(exportedCsv.toPath(), export);
        LOGGER.info("Wrote test file to [{}]", exportedCsv.getAbsolutePath());

        try (final Workbook workbook = WorkbookFactory.create(exportedCsv)) {
            final Sheet sheet = workbook.getSheet(KpiIdentifier.KPI_1_2.getExcelSheetName());
            final Row row = sheet.getRow(1);
            assertThat(row).isNotNull();
            assertThat(row.getCell(0).getStringCellValue()).isEqualTo("home iso");
            assertThat(row.getCell(1).getNumericCellValue()).isEqualTo(2024);
            assertThat(row.getCell(2).getNumericCellValue()).isEqualTo(1);
            assertThat(row.getCell(3).getStringCellValue()).isEqualTo("Sender iso");
            assertThat(row.getCell(4).getStringCellValue()).isEqualTo("Receiving iso");
            assertThat(row.getCell(5).getStringCellValue()).isEqualTo("2024-01-30 11:10:09");
            assertThat(row.getCell(6).getStringCellValue()).isEqualTo("2024-01-31 11:10:09");
            assertThat(row.getCell(7).getStringCellValue()).isEqualTo("PATIENT_IDENTIFICATION_QUERY");
            assertThat(row.getCell(8).getStringCellValue()).isEqualTo("FAILURE");
            assertThat(row.getCell(9).getStringCellValue()).isEqualTo("error description");
            assertThat(row.getCell(10)).isNull();
            assertThat(row.getCell(11).getStringCellValue()).isEqualTo("sender msg id");
            assertThat(row.getCell(12).getStringCellValue()).isEqualTo("receiver msg id");
        }
    }
}