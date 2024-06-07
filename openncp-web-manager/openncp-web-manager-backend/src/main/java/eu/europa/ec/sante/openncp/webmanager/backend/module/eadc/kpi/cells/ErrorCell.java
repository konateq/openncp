package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.cells;

import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.CellIndex;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.persistence.model.Transaction;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Cell;

import java.util.function.Consumer;

public class ErrorCell implements KpiCell {

    private final int index;

    public ErrorCell(final int indexInSheet) {
        this.index = indexInSheet;
    }

    @Override
    public String getHeaderName() {
        return "ERROR";
    }

    @Override
    public CellIndex getCellIndex() {
        return CellIndex.from(index);
    }

    @Override
    public Consumer<Cell> writeCellValue(final Transaction transaction) {
        Validate.notNull(transaction, "Transaction cannot be null");
        
        return (cell) -> {
            Validate.notNull(cell, "Cell cannot be null");
            if (transaction.getTransactionError() != null &&
                    transaction.getTransactionError().getErrorDescription() != null) {
                cell.setCellValue(transaction.getTransactionError().getErrorDescription());
            }
        };
    }
}
