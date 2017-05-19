package vikram.mindtree.com.myevents.teaser;

import android.net.Uri;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by M1032130 on 5/15/2017.
 */

public class TeaserPresenterImpl implements TeaserPresenter, TeaserModel.OnRequestFinishedListener{
    TeaserView tView;
    TeaserModel tModel;

    public TeaserPresenterImpl(TeaserView view) {
        tView = view;
        tModel = new TeaserModelImpl();
    }

    @Override
    public void postTeaser(RequestBody requestBody, File file) {
        if (tView != null) {
            tView.showProgress();
        }
        tModel.postTeaser(requestBody, file, this);
    }

    @Override
    public void downloadImage(String filePath) {
        if (tView != null) {
            tView.showProgressDialog();
        }
        tModel.downloadImage(filePath, this);
    }

    @Override
    public void onDestroy() {
        tView = null;
    }

    @Override
    public void showSuccess() {
        if (tView != null) {
            tView.hideProgress();
            tView.showSuccess();
            tView.hdieProgressDialog();
        }
    }

    @Override
    public void showDownloadMessage(String str) {
        if (tView != null) {
            tView.hideProgress();
            tView.hdieProgressDialog();
            tView.showDownloadMessage(str);
        }
    }

    @Override
    public void showError() {
        if (tView != null) {
            tView.hideProgress();
            tView.showError();
            tView.hdieProgressDialog();
        }
    }

    @Override
    public void showProgressPercentage(int percentage) {
        if (tView != null) {
            tView.showProgressPercentage(percentage);
        }
    }
}
