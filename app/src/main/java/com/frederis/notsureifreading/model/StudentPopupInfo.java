package com.frederis.notsureifreading.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class StudentPopupInfo implements Parcelable {

    private ArrayList<StudentData> studentData;

    public StudentPopupInfo(ArrayList<StudentData> studentData) {
        this.studentData = studentData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(studentData);
    }

    public ArrayList<StudentData> getStudentData() {
        return studentData;
    }

    public static Creator<StudentPopupInfo> CREATOR = new Creator<StudentPopupInfo>() {
        @Override
        public StudentPopupInfo createFromParcel(Parcel source) {
            return new StudentPopupInfo(source.createTypedArrayList(StudentData.CREATOR));
        }

        @Override
        public StudentPopupInfo[] newArray(int size) {
            return new StudentPopupInfo[size];
        }
    };

    public static class StudentData implements Parcelable {

        public long studentId;
        public String studentName;

        public StudentData(long studentId, String studentName) {
            this.studentId = studentId;
            this.studentName = studentName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        public static Creator<StudentData> CREATOR = new Creator<StudentData>() {
            @Override
            public StudentData createFromParcel(Parcel source) {
                return new StudentData(source.readLong(), source.readString());
            }

            @Override
            public StudentData[] newArray(int size) {
                return new StudentData[size];
            }
        };
    }

}
