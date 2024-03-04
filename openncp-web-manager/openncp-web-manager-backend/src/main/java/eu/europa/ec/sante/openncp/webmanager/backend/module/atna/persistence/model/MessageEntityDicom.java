package eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DICOM")
public class MessageEntityDicom extends MessageEntity{

}