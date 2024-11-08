package com.rosokha.todolist.authorization;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Інтерфейс, що представлює собою здатність авторизовуватися
 */
interface AbleToAuthorization {

    String separator = "⋱Ꙟ⋰";
    String userFile = "user.txt";

    /**
     * Перевірка на коректність введених даних
     *
     * @return повертає true якщо дані введені коректно. Плвертає false якщо дані введені не коректно
     */
    boolean checkData();

    /**
     * Хешує пароль користувача методом "SHA-1"
     *
     * @param password пароль, який потрібно захешувати
     * @return захешований пароль
     * @see MessageDigest
     * @see MessageDigest#digest(byte[]) MessageDigest#digest(byte[])
     * @see StringBuilder
     * @see StringBuilder#append(int) StringBuilder#append(int)
     */
    default String hashPassword(String password) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            return password;
        }
        byte[] bytes = sha1.digest(password.getBytes());

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X", b));
        }

        return stringBuilder.toString();
    }
}