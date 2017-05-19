package vikram.mindtree.com.myevents.teaser;

import android.net.Uri;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by M1032130 on 5/15/2017.
 */

public interface TeaserModel {
    public interface OnRequestFinishedListener {
        void showSuccess();
        void showDownloadMessage(String message);
        void showError();
        void showProgressPercentage(int percentage);
    }
    void postTeaser(RequestBody requestBody, File file, OnRequestFinishedListener listener);
    void downloadImage(String filePath, OnRequestFinishedListener listener);
}
