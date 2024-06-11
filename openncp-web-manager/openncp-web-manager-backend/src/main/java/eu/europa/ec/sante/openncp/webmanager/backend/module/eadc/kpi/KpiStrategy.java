package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi;

import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.cells.KpiCell;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.persistence.model.Transaction;

import java.util.List;

public interface KpiStrategy {
    KpiIdentifier getKpiIdentifier();

    default String getKpiSheetName() {
        return getKpiIdentifier().getExcelSheetName();
    }

    List<KpiCell> getKpiCells();

    List<Transaction> filterTransactions(List<Transaction> transactions);
}
