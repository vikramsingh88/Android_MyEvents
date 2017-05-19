package vikram.mindtree.com.myevents.teaser;

import android.net.Uri;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by M1032130 on 5/15/2017.
 */

public interface TeaserView {
    void showProgress();
    void showProgressDialog();
    void hdieProgressDialog();
    void hideProgress();
    void showSuccess();
    void showError();
    void showDownloadMessage(String str);
    void showProgressPercentage(int percentage);
}
