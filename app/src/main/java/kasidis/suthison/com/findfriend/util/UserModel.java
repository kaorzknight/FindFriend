package kasidis.suthison.com.findfriend.util;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable{
    private String nameString, pathAvataString;

    public UserModel() {

    }   // Constructor Getter


    public UserModel(String nameString, String pathAvataString) {
        this.nameString = nameString;
        this.pathAvataString = pathAvataString;
    }   // Constructor Setter

    protected UserModel(Parcel in) {
        nameString = in.readString();
        pathAvataString = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public String getPathAvataString() {
        return pathAvataString;
    }

    public void setPathAvataString(String pathAvataString) {
        this.pathAvataString = pathAvataString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameString);
        dest.writeString(pathAvataString);
    }
}