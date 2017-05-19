package vikram.mindtree.com.myevents;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class NetworkIntentService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String GET_TEAMS = "teams";
    private static final String ADD_TEAM = "add-team";
    private static final String ADD_MEMBER = "add-member";
    private static final String GET_TEAM_MEMBER = "get-team-members";
    private static final String UPDATE_TEAM_MEMBER = "update-team-member";
    private static final String DELETE_TEAM_MEMBER = "delete-team-member";
    private static final String ADD_EVENT = "add-event";
    private static final String GET_EVENTS = "get-events";
    private static final String REG_TOKEN = "reg-token";
    private static final String NOTIFY_USER = "notify-user";
    private static final String ADD_SCORE = "add-score";
    private static final String GET_SCORE = "get-score";
    private static final String LOGIN = "login";
    private static final String INFORM_EVENT = "inform-event";

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");
        url = url.replaceAll(" ", "%20");
        String method = intent.getStringExtra("method");
        String from = intent.getStringExtra("from");
        Bundle bundle = new Bundle();

        if (GET_TEAMS.equalsIgnoreCase(from)) {
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                try {
                    JSONObject result = getDataFromServer(url, method);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (ADD_TEAM.equalsIgnoreCase(from)) {
            //Team name
            String teamName = intent.getStringExtra("teamName");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = populatePostBody(teamName);
                try {
                    JSONObject result = postCreateTeamData(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (ADD_MEMBER.equalsIgnoreCase(from)) {
            String strTeamName = intent.getStringExtra("teamName");
            String strTeamMemberName = intent.getStringExtra("memberName");
            String strAbout = intent.getStringExtra("about");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = populateAddMemberPostBody(strTeamName, strTeamMemberName, strAbout);
                try {
                    JSONObject result = postAddTeamMemberData(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if(UPDATE_TEAM_MEMBER.equalsIgnoreCase(from)) {
            String strTeamName = intent.getStringExtra("teamName");
            String strTeamMemberName = intent.getStringExtra("memberName");
            String strAbout = intent.getStringExtra("about");
            String memberId = intent.getStringExtra("id");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = populateUpdateMemberPostBody(strTeamName, strTeamMemberName, strAbout, memberId);
                try {
                    JSONObject result = updateMemberDetails(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if(DELETE_TEAM_MEMBER.equalsIgnoreCase(from)) {
            String strTeamName = intent.getStringExtra("teamName");
            String memberId = intent.getStringExtra("id");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("teamName", strTeamName);
                    jsonObject.put("id", memberId);
                    System.out.println("Json : " + jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject result = updateMemberDetails(url, method, jsonObject);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (GET_TEAM_MEMBER.equalsIgnoreCase(from)) {
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                try {
                    JSONObject result = getDataFromServer(url, method);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (ADD_EVENT.equalsIgnoreCase(from)) {
            String eventName = intent.getStringExtra("eventName");
            String eventDescription = intent.getStringExtra("eventDescription");
            String eventRule = intent.getStringExtra("eventRule");
            String eventVenue = intent.getStringExtra("eventVenue");
            String eventPoints = intent.getStringExtra("eventPoints");
            String eventTime = intent.getStringExtra("eventTime");
            String eventDate = intent.getStringExtra("eventDate");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = populateEventData(eventName, eventDescription, eventRule, eventVenue, eventPoints, eventTime, eventDate);
                try {
                    JSONObject result = postAddTeamMemberData(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (GET_EVENTS.equalsIgnoreCase(from)) {
            boolean isAdmin = intent.getBooleanExtra("isAdmin", false);
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                try {
                    JSONObject result = getDataFromServer(url+"?isAdmin="+isAdmin, method);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (REG_TOKEN.equalsIgnoreCase(from)) {
            String regId = intent.getStringExtra("regId");
            if (!TextUtils.isEmpty(url)) {
                JSONObject json = new JSONObject();
                try {
                    json.put("deviceId", regId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject result = postAddTeamMemberData(url, method, json);
                    if (null != result) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (NOTIFY_USER.equalsIgnoreCase(from)) {
            String eventName = intent.getStringExtra("eventName");
            String about = intent.getStringExtra("about");
            String time = intent.getStringExtra("time");
            String date = intent.getStringExtra("date");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = new JSONObject();
                try {
                    json.put("eventName", eventName);
                    json.put("about", about);
                    json.put("time", time);
                    json.put("date", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject result = postAddTeamMemberData(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (ADD_SCORE.equalsIgnoreCase(from)) {
            String eventName = intent.getStringExtra("eventName");
            String teamName = intent.getStringExtra("teamName");
            String points = intent.getStringExtra("points");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = new JSONObject();
                try {
                    json.put("eventName", eventName);
                    json.put("teamName", teamName);
                    json.put("points", points);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject result = postAddTeamMemberData(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (GET_SCORE.equalsIgnoreCase(from)) {
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                try {
                    JSONObject result = getDataFromServer(url, method);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                    e.printStackTrace();
                }
            }
        } else if (LOGIN.equalsIgnoreCase(from)) {
            String password = intent.getStringExtra("password");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = new JSONObject();
                try {
                    json.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject result = postLoginData(url, method, json);
                    if (null != result) {
                        if (result.getString("statusMessage").equals("success")) {
                            bundle.putString("result", "success");
                            receiver.send(STATUS_FINISHED, bundle);
                        } else {
                            bundle.putString("result", "error");
                            receiver.send(STATUS_FINISHED, bundle);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        } else if (INFORM_EVENT.equalsIgnoreCase(from)) {
            String comment = intent.getStringExtra("comment");
            if (!TextUtils.isEmpty(url)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                JSONObject json = new JSONObject();
                try {
                    json.put("comment", comment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject result = postLoginData(url, method, json);
                    if (null != result) {
                        bundle.putString("result", result.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
        }

        this.stopSelf();
    }

    private JSONObject populateEventData(String eventName, String eventDescription, String eventRule,
                                         String eventVenue, String eventPoints,
                                         String eventTime, String eventDate) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eventName", eventName);
            jsonObject.put("eventDescription", eventDescription);
            jsonObject.put("eventRule", eventRule);
            jsonObject.put("eventVenue", eventVenue);
            jsonObject.put("eventPoints", eventPoints);
            jsonObject.put("eventTime", eventTime);
            jsonObject.put("eventDate", eventDate);
            System.out.println("Json : " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject postAddTeamMemberData(String strUrl, String method, JSONObject json) throws IOException, Exception {
        InputStream inputStream;
        HttpsURLConnection urlConnection;
        URL url = new URL(strUrl);
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod(method);

        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes(json.toString());
        wr.flush();
        wr.close();

        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 201) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = convertInputStreamToString(inputStream);

            JSONObject results = parseResult(response);

            return results;
        } else {
            throw new Exception();
        }
    }

    private JSONObject postLoginData(String strUrl, String method, JSONObject json) throws IOException, Exception {
        InputStream inputStream;
        HttpsURLConnection urlConnection;
        URL url = new URL(strUrl);
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod(method);

        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes(json.toString());
        wr.flush();
        wr.close();

        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 200 || statusCode == 500 ) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            JSONObject results = parseResult(response);
            return results;
        } else {
            throw new Exception();
        }
    }

    private JSONObject  updateMemberDetails(String strUrl, String method, JSONObject json) throws IOException, Exception{
        InputStream inputStream;
        HttpsURLConnection urlConnection;
        URL url = new URL(strUrl);
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod(method);

        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes(json.toString());
        wr.flush();
        wr.close();

        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 200 || statusCode == 500 ) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            JSONObject results = parseResult(response);
            return results;
        } else {
            throw new Exception();
        }
    }

    private JSONObject populateAddMemberPostBody(String strTeamName, String strTeamMemberName, String strAbout) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("teamName", strTeamName);
            jsonObject.put("memberName", strTeamMemberName);
            jsonObject.put("about", strAbout);
            System.out.println("Json : " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject populateUpdateMemberPostBody(String strTeamName, String strTeamMemberName, String strAbout, String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("teamName", strTeamName);
            jsonObject.put("memberName", strTeamMemberName);
            jsonObject.put("about", strAbout);
            jsonObject.put("id", memberId);
            System.out.println("Json : " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject populatePostBody(String teamName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("teamName", teamName);
            System.out.println("Json : " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String getBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private byte[] readImageFromPath(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        ;
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private JSONObject postCreateTeamData(String strUrl, String requestMethod, JSONObject json) throws IOException, Exception {
        InputStream inputStream;
        HttpsURLConnection urlConnection;
        URL url = new URL(strUrl);
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod(requestMethod);

        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.writeBytes(json.toString());
        wr.flush();
        wr.close();

        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 201) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = convertInputStreamToString(inputStream);

            JSONObject results = parseResult(response);

            return results;
        } else {
            throw new Exception();
        }
    }

    private JSONObject getDataFromServer(String strUrl, String requestMethod) throws IOException, Exception {
        InputStream inputStream;
        HttpsURLConnection urlConnection;
        URL url = new URL(strUrl);
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod(requestMethod);
        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = convertInputStreamToString(inputStream);

            JSONObject results = parseResult(response);

            return results;
        } else {
            throw new Exception();
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        if (null != inputStream) {
            inputStream.close();
        }
        return result;
    }

    private JSONObject parseResult(String result) {
        JSONObject response = null;
        try {
            response = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
