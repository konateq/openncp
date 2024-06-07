package eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi;

import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.kpi.cells.*;
import eu.europa.ec.sante.openncp.webmanager.backend.module.eadc.persistence.model.Transaction;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Kpi1_3Strategy implements KpiStrategy {
    private final List<String> ePrescriptionDataValues =
            List.of(
                    "1.3.6.1.4.1.12559.11.10.1.3.1.1.1",
                    "1.3.6.1.4.1.12559.11.10.1.3.1.1.6"
            );

    @Override
    public KpiIdentifier getKpiIdentifier() {
        return KpiIdentifier.KPI_1_3;
    }

    @Override
    public List<KpiCell> getKpiCells() {
        return List.of(
                new MemberstateCell(0),
                new StartTimeYearCell(1),
                new StartTimeQuarterCell(2),
                new SenderIsoCell(3),
                new ReceiverIsoCell(4),
                new StartTimeDateTimeCell(5),
                new EndTimeDateTimeCell(6),
                new ExchangedDocumentTypeCell(7),
                new ResultCell(8),
                new SenderMessageIdCell(11),
                new ReceiverMessageIdCell(12)
        );
    }


    @Override
    public List<Transaction> filterTransactions(final List<Transaction> transactions) {
        return transactions.stream().filter(transaction ->
                CollectionUtils.isNotEmpty(transaction.getTransactionData())
                        && ePrescriptionDataValues.contains(transaction.getTransactionData().get(0).getDataValue())).collect(Collectors.toList());

    }
}
