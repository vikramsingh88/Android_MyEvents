package vikram.mindtree.com.myevents.teaser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;

/**
 * Created by M1032130 on 5/15/2017.
 */

public interface FileUploadService {
    @Multipart
    @POST("teaser")
    Call<ResponseBody> upload(@Part("description") RequestBody description, @Part MultipartBody.Part file);

    @GET("teaser")
    @Streaming
    public Call<ResponseBody> download();
}
