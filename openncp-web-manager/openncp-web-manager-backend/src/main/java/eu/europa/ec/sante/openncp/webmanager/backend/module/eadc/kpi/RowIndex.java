package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi;

public class RowIndex extends Index {
    private RowIndex(final int currentIndex) {
        super(currentIndex);
    }

    public static RowIndex firstRowAfterHeaderRow() {
        return new RowIndex(1);
    }

    public static RowIndex from(final int index) {
        return new RowIndex(index);
    }

    public boolean isHeaderRow() {
        return getCurrent() == 0;
    }
}
