package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi;

public enum KpiIdentifier {
    KPI_1_2("KPI-1.2"),
    KPI_1_3("KPI-1.3"),
    KPI_1_4("KPI-1.4"),
    KPI_1_5("KPI-1.5"),
    KPI_1_6("KPI-1.6"),
    KPI_1_7("KPI-1.7");

    private final String excelSheetName;

    KpiIdentifier(final String excelSheetName) {
        this.excelSheetName = excelSheetName;
    }

    public String getExcelSheetName() {
        return excelSheetName;
    }
}
