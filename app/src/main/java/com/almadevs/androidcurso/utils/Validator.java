package com.almadevs.androidcurso.utils;

public final class Validator {
    private Validator() {}

    public static boolean isNumeroEmpleado(String text){

        if ( text.isEmpty())
            return false;
       try {
          int intNuneroEmpleado =  Integer.parseInt(text);
           return intNuneroEmpleado >= 90000000 && intNuneroEmpleado <= 99999999;
       }catch (Exception e){
           return  false;
       }

    }

    public static boolean validatePasswordLength(String text) {
        return !text.isEmpty() && text.length() >= 4 && text.length() <= 24;
    }
}


