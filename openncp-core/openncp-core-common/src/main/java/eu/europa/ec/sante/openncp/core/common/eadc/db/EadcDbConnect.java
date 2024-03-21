package eu.europa.ec.sante.openncp.core.common.eadc.db;

import java.sql.Connection;

/**
 * Helper class to handle database connections
 */
public interface EadcDbConnect {

    /**
     * Getter for current connection
     *
     * @return the current connection
     */
    Connection getConnection();

    /**
     * This method will close the connection
     */
    void closeConnection();
}
