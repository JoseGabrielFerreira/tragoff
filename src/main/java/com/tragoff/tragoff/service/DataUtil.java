package com.tragoff.tragoff.service;

import java.util.Calendar;
import java.util.Date;

public class DataUtil {

    // Mesma data às 00:00:00
    public static Date inicioDoDia(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    // Mesma data às 23:59:59
    public static Date fimDoDia(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    // Soma dias (use número negativo para voltar no tempo)
    public static Date somarDias(Date data, int dias) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }
}