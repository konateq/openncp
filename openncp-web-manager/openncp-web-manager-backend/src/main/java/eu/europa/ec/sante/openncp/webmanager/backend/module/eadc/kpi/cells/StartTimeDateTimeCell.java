package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.cells;

import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.CellIndex;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.persistence.model.Transaction;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Cell;

import java.util.function.Consumer;

public class StartTimeDateTimeCell implements KpiCell {

    private final int index;

    public StartTimeDateTimeCell(final int indexInSheet) {
        this.index = indexInSheet;
    }

    @Override
    public String getHeaderName() {
        return "START (time stamp)";
    }

    @Override
    public CellIndex getCellIndex() {
        return CellIndex.from(index);
    }

    @Override
    public Consumer<Cell> writeCellValue(final Transaction transaction) {
        Validate.notNull(transaction, "Transaction cannot be null");
        
        return (cell) -> cell.setCellValue(transaction.getStartTime() != null ? DATE_TIME_FORMATTER.format(transaction.getStartTime().atZone(ZONE_ID)) : "");
    }
}
