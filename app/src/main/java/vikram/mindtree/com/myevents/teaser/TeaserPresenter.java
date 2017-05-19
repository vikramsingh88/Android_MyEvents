package vikram.mindtree.com.myevents.teaser;

import android.net.Uri;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by M1032130 on 5/15/2017.
 */

public interface TeaserPresenter {
    void postTeaser(RequestBody requestBody, File file);
    void downloadImage(String filePath);
    void onDestroy();
}
