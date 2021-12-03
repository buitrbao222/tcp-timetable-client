package Utils;

import DTO.ClassSession;
import DTO.Subject;
import DTO.SubjectClass;
import DTO.TimeTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class Api {
    private static ArrayList<TimeTable> addTimeTables(JSONArray jsonTimeTables) {
        ArrayList<TimeTable> timeTables = new ArrayList<>();

        for (Object obj : jsonTimeTables) {
            JSONObject jsonObject = (JSONObject) obj;

            TimeTable timeTable = new TimeTable();

            JSONArray jsonSubjects = jsonObject.getJSONArray("subjects");
            ArrayList<Subject> subjects = addSubjects(jsonSubjects);

            timeTable.afternoon = Boolean.parseBoolean(jsonObject.getString("afternoon"));
            timeTable.daysOn = Arrays.stream(((jsonObject.getString("daysOn")).split(",")))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            timeTable.subjects = subjects.toArray(new Subject[0]);
            timeTable.numDaysOn = Integer.parseInt(jsonObject.getString("numDaysOn"));
            timeTable.morning = Boolean.parseBoolean(jsonObject.getString("morning"));

            timeTables.add(timeTable);
        }
        return timeTables;
    }

    private static ArrayList<Subject> addSubjects(JSONArray jsonSubjects) {
        ArrayList<Subject> subjects = new ArrayList<>();

        for (Object obj : jsonSubjects) {
            JSONObject jsonObject = (JSONObject) obj;

            Subject subject = new Subject();

            subject.subjectId = jsonObject.getString("subjectId");
            subject.subjectName = jsonObject.getString("subjectName");
            subject.credits = jsonObject.getString("credits");

            JSONArray jsonClasses = jsonObject.getJSONArray("classes");
            ArrayList<SubjectClass> subjectClasses = addSubjectClasses(jsonClasses);
            subject.classes = subjectClasses.toArray(new SubjectClass[0]);

            subjects.add(subject);
        }
        return subjects;
    }

    private static ArrayList<SubjectClass> addSubjectClasses(JSONArray jsonSubjectClasses) {
        ArrayList<SubjectClass> subjectClasses = new ArrayList<>();

        for (Object obj : jsonSubjectClasses) {

            JSONObject jsonObject = (JSONObject) obj;

            SubjectClass subjectClass = new SubjectClass();

            subjectClass.group = jsonObject.getString("group");

            JSONArray jsonSessions = jsonObject.getJSONArray("sessions");
            ArrayList<ClassSession> classSessions = addClassSessions(jsonSessions);
            subjectClass.sessions = classSessions.toArray(new ClassSession[0]);

            subjectClasses.add(subjectClass);
        }
        return subjectClasses;
    }

    private static ArrayList<ClassSession> addClassSessions(JSONArray jsonClassSessions) {
        ArrayList<ClassSession> classSessions = new ArrayList<>();

        for (Object obj : jsonClassSessions) {
            JSONObject jsonObject = (JSONObject) obj;

            ClassSession classSession = new ClassSession();

            classSession.start = jsonObject.getInt("start");
            classSession.day = jsonObject.getInt("day");
            classSession.length = jsonObject.getInt("length");
            classSession.room = jsonObject.getString("room");
            classSessions.add(classSession);
        }
        return classSessions;
    }

    public static ArrayList<TimeTable> getTimeTables(String params) throws IOException {
        ArrayList<TimeTable> timeTables;
        BufferedReader in = null;
        String line = "";
        String api = "https://timetable-heroku.herokuapp.com/api" + params;

        try {
            URLConnection urlConnection = new URL(api).openConnection();
            InputStream is = urlConnection.getInputStream();
            in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            line = in.readLine();

            if (line != null) {
                JSONObject object = new JSONObject(line);

                JSONArray jsonTimeTables = object.getJSONArray("timeTables");
                timeTables = addTimeTables(jsonTimeTables);

                System.out.println(api);
                System.out.println(jsonTimeTables);

                in.close();
                return timeTables;
            }
        } catch (IOException e) {
            in.close();
            e.printStackTrace();
        }
        in.close();
        return null;
    }
}
