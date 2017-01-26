package ismartdev.mn.bundan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDeteailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deteail);

    }
    private void getFbDatas(){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.e("data", response.getRawResponse() + "");
                        try {
                            addToFirebase(object, getUid());

                        } catch (JSONException e) {
                            Toast.makeText(FullscreenActivity.this, e.getMessage() + "", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender,education,birthday,picture.type(large),email,friends,work,likes");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
