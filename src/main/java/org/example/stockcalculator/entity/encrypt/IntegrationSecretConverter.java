package org.example.stockcalculator.entity.encrypt;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IntegrationSecretConverter implements AttributeConverter<String, String> {

    private static final String SECRET_KEY = System.getenv("SECRETS_ENCRYPTION_KEY");

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return CryptoUtils.encrypt(attribute, SECRET_KEY);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return CryptoUtils.decrypt(dbData, SECRET_KEY);
    }
}
