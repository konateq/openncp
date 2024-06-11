package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi;

public class CellIndex extends Index {
    private CellIndex(final int currentIndex) {
        super(currentIndex);
    }

    public static CellIndex from(final int index) {
        return new CellIndex(index);
    }
}
