package mas.alrm.alrm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class CalendarLinkActivity extends AppCompatActivity {

    TextView linkTextView;
    EditText codeEditText;
    Button authButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_link);

        linkTextView = (TextView) findViewById(R.id.calendar_link);
        linkTextView.setClickable(true);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        String url = "http://alrm.str.at:5000/getUrl";
        JsonObjectRequest urlRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String linkText = "<a href='"
                                        + response.getString("url")
                                        + "'>Click Here to get auth code</a>";
                                linkTextView.setText(Html.fromHtml(linkText));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", error.getMessage());
                        }
                    }
                );
        RequestQueueSingleton.getInstance(this).addToRequestQueue(urlRequest);

        codeEditText = (EditText) findViewById(R.id.calendar_code);
        authButton = (Button) findViewById(R.id.button_calendar_auth);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEditText.getText().toString();
                if (code.length() == 0) {
                    Toast.makeText(CalendarLinkActivity.this, "Form is empty", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("Auth", code);
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("code", code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String postUrl = "http://alrm.str.at:5000/getToken";
                    JsonObjectRequest tokenRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            postUrl,
                            requestBody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String fbUid = FirebaseAuth.getInstance().
                                                getCurrentUser().getUid();
                                        String access_token = response.getString("access_token");
                                        String refresh_token = response.getString("refresh_token");
                                        int expiry_date = response.getInt("expiry_date");
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference ref = db.getReference("users/" + fbUid);
                                        ref.child("token").child("access_token").setValue(access_token);
                                        ref.child("token").child("refresh_token").setValue(refresh_token);
                                        ref.child("token").child("expiry_date").setValue(expiry_date);
                                        Intent intent = new Intent(CalendarLinkActivity.this,
                                                UpcomingAlarmsActivity.class);
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Auth", error.getMessage());
                                }
                            }
                    );
                    RequestQueueSingleton.getInstance(CalendarLinkActivity.this).addToRequestQueue(tokenRequest);
                }
            }
        });
    }
}
