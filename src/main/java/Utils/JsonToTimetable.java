package Utils;

import DTO.ClassSession;
import DTO.Subject;
import DTO.SubjectClass;
import DTO.Timetable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class JsonToTimetable {
    private static ArrayList<Timetable> addTimetables(JSONArray jsonTimetables) {
        ArrayList<Timetable> timetables = new ArrayList<>();

        for (Object obj : jsonTimetables) {
            JSONObject jsonObject = (JSONObject) obj;

            Timetable timeTable = new Timetable();

            JSONArray jsonSubjects = jsonObject.getJSONArray("subjects");
            ArrayList<Subject> subjects = addSubjects(jsonSubjects);

            timeTable.afternoon = Boolean.parseBoolean(jsonObject.getString("afternoon"));
            timeTable.daysOn = jsonObject.getString("daysOn");
            timeTable.subjects = subjects.toArray(new Subject[0]);
            timeTable.numDaysOn = Integer.parseInt(jsonObject.getString("numDaysOn"));
            timeTable.morning = Boolean.parseBoolean(jsonObject.getString("morning"));

            timetables.add(timeTable);
        }
        return timetables;
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

            JSONArray weeks = jsonObject.getJSONArray("week");
            int length = weeks.length();
            classSession.weeks = new boolean[length];

            for (int i = 0; i < length; i++) {
                classSession.weeks[i] = (boolean) weeks.get(i);
            }

            classSession.start = jsonObject.getInt("start");
            classSession.day = jsonObject.getInt("day");
            classSession.length = jsonObject.getInt("length");
            classSession.room = jsonObject.getString("room");
            classSessions.add(classSession);
        }
        return classSessions;
    }

    public static ArrayList<Timetable> convert(String data) throws IOException {
        ArrayList<Timetable> timetables = new ArrayList<>();

        if (!data.isEmpty()) {
            JSONObject object = new JSONObject(data);

            JSONArray jsonTimetables = object.getJSONArray("timeTables");

            timetables = addTimetables(jsonTimetables);
        }
        return timetables;
    }
}
