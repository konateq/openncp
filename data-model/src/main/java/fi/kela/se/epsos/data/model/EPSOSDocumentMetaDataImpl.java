package fi.kela.se.epsos.data.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

public class EPSOSDocumentMetaDataImpl implements EPSOSDocumentMetaData {

    private final String id;
    private final String patientId;
    private final int documentFormat;
    private final Date effectiveDate;
    private final String classCode;
    private final String repositoryId;
    private final String title;
    private final String author;

    public EPSOSDocumentMetaDataImpl(String id, String patientId, int documentFormat, Date effectiveDate,
                                     String classCode, String repositoryId, String title, String author) {

        this.id = id;
        this.patientId = patientId;
        this.documentFormat = documentFormat;
        this.effectiveDate = effectiveDate;
        this.classCode = classCode;
        this.repositoryId = repositoryId;
        this.title = title;
        this.author = author;
    }

    public EPSOSDocumentMetaDataImpl(EPSOSDocumentMetaData metaData) {

        this.id = metaData.getId();
        this.patientId = metaData.getPatientId();
        this.documentFormat = metaData.getFormat();
        this.effectiveDate = metaData.getEffectiveTime();
        this.classCode = metaData.getClassCode();
        this.repositoryId = metaData.getRepositoryId();
        this.title = metaData.getTitle();
        this.author = metaData.getAuthor();
    }

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public int getFormat() {
        return documentFormat;
    }

    public Date getEffectiveTime() {
        return effectiveDate;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("patientId", patientId)
                .append("documentFormat", documentFormat)
                .append("effectiveDate", effectiveDate)
                .append("classCode", classCode)
                .append("repositoryId", repositoryId)
                .append("title", title)
                .append("author", author)
                .toString();
    }
}
