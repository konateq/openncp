package org.openhealthtools.openatna.audit.util;

import org.openhealthtools.openatna.audit.persistence.AtnaPersistenceException;
import org.openhealthtools.openatna.audit.persistence.util.DataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class EntityLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityLoader.class);

    private EntityLoader() {
    }

    public static void main(String[] args) {
        URL codes = CodeLoader.class.getResource("/conf/entities.xml");
        LOGGER.info("EntityLoader: entities are being read from: {}", codes);
        try {
            assert codes != null;
            InputStream in = codes.openStream();
            DataReader reader = new DataReader(in);
            reader.parse();
        } catch (IOException | AtnaPersistenceException e) {
            LOGGER.error("Could not load data!", e);
        }
    }


}
