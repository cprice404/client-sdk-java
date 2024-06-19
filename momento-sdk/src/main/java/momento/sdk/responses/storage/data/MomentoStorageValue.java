package momento.sdk.responses.storage.data;

import momento.sdk.exceptions.ClientSdkException;

public class MomentoStorageValue {
    private final Object value;
    private final ValueType valueType;

    private MomentoStorageValue(Object value, ValueType valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    public static MomentoStorageValue of(byte[] value) {
        return new MomentoStorageValue(value, ValueType.BYTE_ARRAY);
    }

    public static MomentoStorageValue of(String value) {
        return new MomentoStorageValue(value, ValueType.STRING);
    }

    public static MomentoStorageValue of(long value) {
        return new MomentoStorageValue(value, ValueType.LONG);
    }

    public static MomentoStorageValue of(double value) {
        return new MomentoStorageValue(value, ValueType.DOUBLE);
    }

    public ValueType getType() {
        return valueType;
    }

    public byte[] getByteArray() {
        ensureCorrectTypeOrThrowException(ValueType.BYTE_ARRAY, valueType);
        return (byte[]) value;
    }

    public String getString() {
        ensureCorrectTypeOrThrowException(ValueType.STRING, valueType);
        return (String) value;
    }

    public long getLong() {
        ensureCorrectTypeOrThrowException(ValueType.LONG, valueType);
        return (long) value;
    }

    public double getDouble() {
        ensureCorrectTypeOrThrowException(ValueType.DOUBLE, valueType);
        return (double) value;
    }

    private void ensureCorrectTypeOrThrowException(ValueType requested, ValueType actual) {
        if (requested != actual) {
        // In a regular Java context, ClassCastException or IllegalStateException could be
        // appropriate here
        throw new ClientSdkException(
            String.format(
                "Value is not a %s but was: %s".format(requested.toString(), actual.toString())));
        }
    }
}
