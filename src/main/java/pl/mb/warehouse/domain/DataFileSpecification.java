package pl.mb.warehouse.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class describes data file upon which a data set will be created.
 *
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class DataFileSpecification {

    /**
     * File location url.
     */
    private final URL location;

    /**
     * Character used to separate fields in a single row.
     */
    private final Character fieldSeparator;

    /**
     * Character(s) used to mark a row end.
     */
    private final String lineEnd;

    /**
     * Defines date format  used in this file.
     */
    private final String localDateFormat;

    /**
     * Defines date time format used in this file.
     */
    private final String localDateTimeFormat;

    /**
     * Indicates whether this file has a header or has not.
     */
    private final boolean header;

    public static DataFileSpecificationBuilder builder() {
        return new DataFileSpecificationBuilder();
    }

    public static class DataFileSpecificationBuilder {
        private URL locationUrl = null;
        private Character fieldSeparator = ',';
        private String lineEnd = "\n";
        private String localDateFormat =  "yyyy/MM/dd HH:mm:ss";
        private String localDateTimeFormat = "yyyy/MM/dd HH:mm:ss";
        private boolean header = true;

        DataFileSpecificationBuilder() {
        }

        public DataFileSpecification.DataFileSpecificationBuilder location(String locationUrl) {
            try {
                this.locationUrl =  new URL(locationUrl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentDomainException("Location url is not valid.");
            }
            return this;
        }

        public DataFileSpecification.DataFileSpecificationBuilder fieldSeparator(Character fieldSeparator) {
            DomainAsserts.assertArgumentNotNull(fieldSeparator, "Field separator cannot be null.");
            this.fieldSeparator = fieldSeparator;
            return this;
        }

        public DataFileSpecification.DataFileSpecificationBuilder lineEnd(String lineEnd) {
            DomainAsserts.assertArgumentNotEmpty(lineEnd, "Line end cannot be null.");
            this.lineEnd = lineEnd;
            return this;
        }

        public DataFileSpecification.DataFileSpecificationBuilder localDateFormat(String localDateFormat) {
            DomainAsserts.assertArgumentNotNull(localDateFormat, "Local date format be null.");
            this.localDateFormat = localDateFormat;
            return this;
        }

        public DataFileSpecification.DataFileSpecificationBuilder localDateTimeFormat(String localDateTimeFormat) {
            DomainAsserts.assertArgumentNotNull(localDateTimeFormat, "Local date time format be null.");
            this.localDateTimeFormat = localDateTimeFormat;
            return this;
        }

        public DataFileSpecification.DataFileSpecificationBuilder header(boolean header) {
            this.header = header;
            return this;
        }

        public DataFileSpecification build() {
            DomainAsserts.assertArgumentNotNull(locationUrl, "File location url mustn't be null");
            return new DataFileSpecification(locationUrl, fieldSeparator, lineEnd, localDateFormat, localDateTimeFormat, header);
        }

        public String toString() {
            return "DataFileSpecification.DataFileSpecificationBuilder(fieldSeparator=" + this.fieldSeparator + ", lineEnd=" + this.lineEnd + ", localDateFormat=" + this.localDateFormat + ", localDateTimeFormat=" + this.localDateTimeFormat + ", header=" + this.header + ")";
        }
    }
}
