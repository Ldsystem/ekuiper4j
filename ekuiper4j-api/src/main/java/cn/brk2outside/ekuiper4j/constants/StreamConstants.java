package cn.brk2outside.ekuiper4j.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p> </p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2024/5/7
 */
public interface StreamConstants {

    @Getter
    @RequiredArgsConstructor
    enum DataType {
        BIGINT(DataTypes.BIGINT),
        FLOAT(DataTypes.FLOAT),
        STRING(DataTypes.STRING),
        DATETIME(DataTypes.DATETIME),
        BOOLEAN(DataTypes.BOOLEAN),
        /** byte array */
        BYTEA(DataTypes.BYTEA),
        ARRAY(DataTypes.ARRAY),
        STRUCT(DataTypes.STRUCT)
        ;

        private final String dataType;

    }

    interface DataTypes {
        String BIGINT = "bigint";
        String FLOAT = "float";
        String STRING = "string";
        String BOOLEAN = "boolean";
        String DATETIME = "datetime";
        String BYTEA = "bytea";
        String ARRAY = "array";
        String STRUCT = "struct";
    }

    @Getter
    enum StreamFormat {
        JSON(StreamFormats.JSON),
        PROTOBUF(StreamFormats.PROTOBUF),
        BINARY(StreamFormats.BINARY)
        ;

        private final String format;

        StreamFormat(String format) {
            this.format = format;
        }
    }

    interface StreamFormats {
        String JSON = "JSON";
        String PROTOBUF = "PROTOBUF";
        String BINARY = "BINARY";
    }



}
