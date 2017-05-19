package vikram.mindtree.com.myevents.teaser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import vikram.mindtree.com.myevents.Constants;
import vikram.mindtree.com.myevents.R;

/**
 * Created by M1032130 on 5/15/2017.
 */

public class TeaserActivity extends AppCompatActivity implements TeaserView{
    private ImageView mImageViewBanner;
    private Button mButtonNotify;
    private EditText mEditTextBannerName;
    private ProgressBar mProgressBar;
    private ProgressDialog mProgressDialog ;
    Bitmap selectedImage = null;
    private final int SELECT_PHOTO = 1;
    Uri imageUri;
    TeaserPresenter tPresenter;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar2);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("Downloading ...");

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        tPresenter = new TeaserPresenterImpl(this);

        mImageViewBanner = (ImageView)findViewById(R.id.img_banner);
        mEditTextBannerName = (EditText)findViewById(R.id.edit_primer_name);
        mButtonNotify = (Button)findViewById(R.id.btn_send_primer);

        if (path != null){
            mEditTextBannerName.setVisibility(View.GONE);
            mButtonNotify.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(Constants.GET_TEASER)
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .fit()
                    .centerInside()
                    .into(mImageViewBanner);
        }

        mImageViewBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path == null) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            }
        });

        mButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null && !mEditTextBannerName.getText().toString().equals("")){
                    File file = new File(getRealPathFromUri(TeaserActivity.this, imageUri));
                    RequestBody requestFile = RequestBody.create( MediaType.parse(getContentResolver().getType(imageUri)),file);
                    postTeaser(requestFile, file);
                } else {
                    Toast.makeText(TeaserActivity.this, R.string.image_required, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teaser, menu);
        if (path == null) {
            invalidateOptionsMenu();
            menu.findItem(R.id.action_download).setVisible(false);
        } else {
            invalidateOptionsMenu();
            menu.findItem(R.id.action_download).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_download :
                download();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void download() {
        String timeStamp = String.valueOf(new Date().getTime());
        String path = getExternalFilesDir(null) + File.separator + "fun"+timeStamp+".png";
        tPresenter.downloadImage(path);
        Log.d("TeaserActivity", path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    imageUri = imageReturnedIntent.getData();
                    Picasso.with(this).load(imageUri)
                            .fit()
                            .centerInside()
                            .into(mImageViewBanner);
                }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void postTeaser(RequestBody requestBody, File file) {
        tPresenter.postTeaser(requestBody, file);
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressDialog() {
        if (!mProgressDialog.isShowing())
                mProgressDialog.show();
    }

    @Override
    public void hdieProgressDialog() {
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSuccess() {
        Toast.makeText(this, "File uploaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError() {
        Toast.makeText(this, "Failed file upload", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDownloadMessage(String str) {
        Toast.makeText(this, "File downloaded at "+str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressPercentage(int percentage) {
        mProgressDialog.setProgress(percentage);
    }
}