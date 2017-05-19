package vikram.mindtree.com.myevents.teaser;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by M1032130 on 5/15/2017.
 */

public class TeaserModelImpl implements TeaserModel{
    @Override
    public void postTeaser(RequestBody requestBody, File file, OnRequestFinishedListener listener) {
        uploadFile(requestBody, file, listener);
    }

    @Override
    public void downloadImage(String filePath, OnRequestFinishedListener listener) {
        download(filePath, listener);
    }

    private void uploadFile(RequestBody requestBody, File file, final OnRequestFinishedListener listener) {
        // create upload service client
        FileUploadService service = ServiceGenerator.getClient().create(FileUploadService.class);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestBody);
        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
        // finally, execute the request
        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                listener.showSuccess();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                if(t instanceof SocketTimeoutException){
                    Log.e("Upload error:", "Socket Time out. Please try again.");
                }
                listener.showError();
            }
        });
    }

    private void download(final String filePath, final OnRequestFinishedListener listener) {
        FileUploadService service = ServiceGenerator.getClient().create(FileUploadService.class);

        Call<ResponseBody> call = service.download();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask(filePath, listener);
                    downloadFileAsyncTask.execute(response.body().byteStream());
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    listener.showError();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call,Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    private class DownloadFileAsyncTask extends AsyncTask<InputStream, Integer, Boolean> {
        String path;
        OnRequestFinishedListener listener;
        DownloadFileAsyncTask(String path, OnRequestFinishedListener listener) {
            this.path = path;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(InputStream... params) {
            try {
                Log.d("DownloadImage", "Reading and writing file");
                InputStream in = params[0];
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(path);
                    int c;
                    long total = 0;
                    while ((c = in.read()) != -1) {
                        total += c;
                        if (in.available() > 0){
                            publishProgress((int)((total*100)/in.available()));
                        }
                        out.write(c);
                    }
                }
                catch (IOException e) {
                    Log.d("DownloadImage",e.toString());
                    return false;
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
                return true;

            } catch (IOException e) {
                Log.d("DownloadImage",e.toString());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            listener.showProgressPercentage(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                listener.showDownloadMessage(path);
            } else {
                listener.showError();
            }
        }
    }
}
