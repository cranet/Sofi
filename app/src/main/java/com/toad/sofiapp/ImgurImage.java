package com.toad.sofiapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ImgurImage implements Parcelable {

    public String id;
    public String title;

    protected ImgurImage(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    public static final Creator<ImgurImage> CREATOR = new Creator<ImgurImage>() {
        @Override
        public ImgurImage createFromParcel(Parcel in) {
            return new ImgurImage(in);
        }

        @Override
        public ImgurImage[] newArray(int size) {
            return new ImgurImage[size];
        }
    };

    public ImgurImage() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);

    }
}
