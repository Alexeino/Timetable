package it.mobile.timetable.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.annotation.SuppressLint;

public class TimetableImpl implements Timetable {

	private Map<String, Course> coursesByName;

	private TimetableImpl() {
		coursesByName = new HashMap<String, Course>();
	}

	@Override
	public Collection<Course> getCourses() {
		return coursesByName.values();
	}

	@Override
	public Collection<Lecture> getLectures() {
		List<Lecture> result = new LinkedList<Lecture>();
		for (Course course : coursesByName.values()) {
			result.addAll(course.getLectures());
		}
		return result;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public Collection<Lecture> filter(String courseName, String teacherName) {
		List<Lecture> result = new LinkedList<Lecture>();
		for (Course course : coursesByName.values()) {
			boolean nameMatches = (courseName == null || course.getName()
					.toLowerCase().contains((courseName.toLowerCase())));
			boolean teacherMatches = (teacherName == null || course
					.getTeacher().toLowerCase()
					.contains(teacherName.toLowerCase()));
			if (!nameMatches || !teacherMatches) {
				continue;
			}
			result.addAll(course.getLectures());
		}
		return result;
	}

	public static TimetableImpl newInstance(InputStream jsonFileStream) {
		TimetableImpl timetable = new TimetableImpl();
		try {
			String content = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(
					jsonFileStream));
			String l = null;
			while ((l = br.readLine()) != null) {
				content += l;
			}
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(content);

			JSONArray coursesArray = (JSONArray) jsonObject.get("courses");
			for (int i = 0; i < coursesArray.size(); i++) {
				JSONObject courseObj = (JSONObject) coursesArray.get(i);
				String name = (String) courseObj.get("name");
				String teacher = (String) courseObj.get("teacher");
				String description = (String) courseObj.get("description");
				Course course = new Course(name, teacher, description);
				timetable.coursesByName.put(name, course);
			}

			JSONArray lecturesArray = (JSONArray) jsonObject.get("lectures");
			for (int i = 0; i < lecturesArray.size(); i++) {
				JSONObject lectureObj = (JSONObject) lecturesArray.get(i);
				long day = (Long) lectureObj.get("day");
				String classroom = (String) lectureObj.get("classroom");
				String start = (String) lectureObj.get("start");
				String end = (String) lectureObj.get("end");
				String courseName = (String) lectureObj.get("course");
				StringTokenizer st = null;
				st = new StringTokenizer(start, ":");
				Time startTime = new Time(Integer.parseInt(st.nextToken()),
						Integer.parseInt(st.nextToken()));
				st = new StringTokenizer(end, ":");
				Time endTime = new Time(Integer.parseInt(st.nextToken()),
						Integer.parseInt(st.nextToken()));
				Course myCourse = timetable.coursesByName.get(courseName);
				myCourse.getLectures().add(
						new Lecture(myCourse, startTime, endTime, (int)day,
								classroom));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return timetable;
	}
	
}
