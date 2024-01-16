package com.example.pronuntiapp_mistificatori;

public class getCharacter {

    public int get(String value){
        switch (value){
            case "A1":
                return R.drawable.personaggio_a;
            case "A2":
                return R.drawable.personaggio_b;
            case "A3":
                return R.drawable.personaggio_c;
            case "A4":
                return R.drawable.personaggio_d;
            case "A5":
                return R.drawable.personaggio_e;
            case "B1":
                return R.drawable.personaggio_cool;
            case "B2":
                return R.drawable.personaggio_love;
            case "B3":
                return R.drawable.personaggio_sciolto;
            case "B4":
                return R.drawable.personaggio_star;
            case "B5":
                return R.drawable.personaggio_wow;
        }
        return R.drawable.personaggio_a;
    }
}
