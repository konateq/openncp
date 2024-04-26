package eu.europa.ec.sante.openncp.webmanager.backend.module.atna.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("RFC3881")
public class MessageEntityRfc3881 extends MessageEntity{

}