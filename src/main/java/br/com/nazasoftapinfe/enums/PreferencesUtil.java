package br.com.nazasoftapinfe.enums;

import java.util.prefs.Preferences;
public class PreferencesUtil {

    private PreferencesUtil(){}

    private static Preferences state;

    public static Preferences getState() {
        return state;
    }

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
    }

    public static final String THEME = "java-danfe.tema";
    public static  final String LAST_FOLDER = "java-danfe.ultima-pasta";
}