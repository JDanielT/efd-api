package br.com.jbssistemas.efdclient.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class EventoUtil {

    public static String getEventoId(String documento, LocalDateTime date) {
        return "ID1" +
               StringUtils.rightPad(documento, 14, "0") +
               date.getYear() +
               String.format("%02d", date.getMonthValue()) +
               String.format("%02d", date.getDayOfMonth()) +
               String.format("%02d", date.getHour()) +
               String.format("%02d", date.getMinute()) +
               String.format("%02d", date.getSecond()) +
               "00001";
    }

}
