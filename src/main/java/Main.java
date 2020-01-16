import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    List<String> passwords = new LinkedList<>();
    List<String> departments = new ArrayList<>();
    List<String> nominations = new ArrayList<>();


    String csvString;
    String groupString;

    public static void main(String[] args) {
        try {
            createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void createFile() throws IOException {

        File json = new File("src/main/resources/JSONbody.txt");
        File csv = new File("src/main/resources/groups.txt");
        File groups = new File("src/main/resources/ggg.txt");

        json.createNewFile();

        FileWriter writer = new FileWriter(json);
        FileWriter writerCsv = new FileWriter(csv);
        FileWriter writerGroups = new FileWriter(groups);

        json.toPath();
        Main main = start();
        writer.write(main.buildJson());
        writer.flush();
        writerCsv.write(main.csvString);
        writerCsv.flush();
        writerGroups.write(main.groupString);
        writerGroups.flush();
        writer.close();
        writerCsv.close();
        writerGroups.close();
    }

    static Main start() {
        Main main = new Main();
        main.fillDepartmets();
        main.fillNominations();
        main.fillPasswords();
        return main;
    }

    String buildJson() {

        StringBuilder stringBuilder = new StringBuilder("var dataObjects = \n[");
        StringBuilder csvBuilder = new StringBuilder("description,enrolmentkey,groupname\n");
        StringBuilder groupBuilder = new StringBuilder();
        htmlDecoder decoder = new htmlDecoder();
        for (String department : departments) {
            stringBuilder.append("{\nfName: \"");
            stringBuilder.append(department);
            stringBuilder.append("\",\nnomination: [");
            String city = cityPattern(department);
            final int rest = 37 - city.length();
            for (String nomination : nominations) {
                stringBuilder.append("{\nnName: \"");
                stringBuilder.append(nomination);
                stringBuilder.append("\",\npassword: \"");
                stringBuilder.append(passwords.get(0));
                stringBuilder.append("\"\n}, ");

                csvBuilder.append(decoder.code(
                        cityPattern(department)));
                csvBuilder.append(" ");
                csvBuilder.append(decoder.code(nomination));
                csvBuilder.append(",");
                csvBuilder.append(passwords.get(0));
                passwords.remove((0));
                csvBuilder.append(",");
                csvBuilder.append(decoder.code(subString(nomination, rest)));
                csvBuilder.append(" ");
                csvBuilder.append(decoder.code(city));
                csvBuilder.append("\n");

                groupBuilder.append(subString(nomination, rest));
                groupBuilder.append(" ");
                groupBuilder.append(city);
                groupBuilder.append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("]\n},\n");
        }

        csvString = csvBuilder.toString();
        groupString = groupBuilder.toString();

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    void fillPasswords() {
        int passwordLength = 6;
        Random random = new Random();
        String symbols = "qwertyuiopasdfghjklzxcvbnm1234567890";
        Set<String> unique = new HashSet<>();
        while (unique.size() < departments.size() * nominations.size()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < passwordLength; i++) {
                stringBuilder.append(symbols.charAt(random.nextInt(symbols.length())));
            }
            unique.add(stringBuilder.toString());
        }
        passwords.addAll(unique);
    }

    void fillDepartmets() {
        String[] filials = new String[]{
                "Дальневосточный филиал (г.Хабаровск)",
                "Восточно-Сибирский филиал (г. Иркутск)",
                "Западно-Сибирский филиал (г. Томск)",
                "Казанский филиал (г. Казань)",
                "г.Москва",
                "Приволжский филиал (г. Нижний Новгород)",
                "Ростовский филиал (г. Ростов-на-Дону)",
                "Северо-Западный филиал (г. Санкт-Петербург)",
                "Северо-Кавказский филиал (г. Краснодар)",
                "Уральский филиал (г. Челябинск)",
                "Центральный филиал (г. Воронеж)",
                "Крымский филиал (г. Симферополь)"};
        departments.addAll(Arrays.asList(filials));
    }

    void fillNominations() {
        String[] nominations = new String[]{
                "Административное право",
                "Гражданское право",
                "Гражданское и административное судопроизводство",
                "Земельное и экологическое право",
                "Информационное право",
                "История права и государства",
                "Конституционное право",
                "Международное право",
                "Организация судебной и правоохранительной деятельности",
                "Правовое обеспечение экономической деятельности",
                "Язык и право",
                "Судебная экспертиза и криминалистика",
                "Теория права, государства и судебной власти",
                "Трудовое право и право социального обеспечения",
                "Уголовное право",
                "Уголовно-процессуальное право",
                "Философия и социально-гуманитарные дисциплины",
                "Финансовое право",
                "Экономика и управление недвижимостью",
                "Экономика"
        };
        this.nominations.addAll(Arrays.asList(nominations));
    }

    String cityPattern(String department) {
        Pattern pattern = Pattern.compile("г\\.( ?)");
        Matcher matcher = pattern.matcher(department);
        if (matcher.find()) {
            String city = department.substring(matcher.start()).replace(")", "");
            return pattern.matcher(city).replaceAll("");
        }
        return department;
    }

    String subString(String str, int rest) {
        if (str.length() < rest) {
            return str;
        } else {
            return str.substring(0, rest) + ".";
        }
    }
}
