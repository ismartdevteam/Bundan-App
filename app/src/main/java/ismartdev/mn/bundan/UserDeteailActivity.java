package ismartdev.mn.bundan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDeteailActivity extends AppCompatActivity {
    private ImageView imageOne;
    private ImageView imageOneBtn;
    private ImageView imageTwo;
    private ImageView imageTwoBtn;
    private ImageView imageThree;
    private ImageView imageThreeBtn;
    private ImageView imageFour;
    private ImageView imageFourBtn;
    private ImageView imageFive;
    private ImageView imageFiveBtn;
    private ImageView imageSix;
    private ImageView imageSixBtn;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deteail);

        imageOne=(ImageView)findViewById(R.id.user_imagef);
        imageOneBtn=(ImageView)findViewById(R.id.user_imagef_Btn
        ); ;
        imageTwo=(ImageView)findViewById(R.id.user_imagetwo); ;
        imageTwoBtn=(ImageView)findViewById(R.id.user_imagetwo_Btn); ;
        imageThree=(ImageView)findViewById(R.id.user_imagethree); ;
        imageThreeBtn=(ImageView)findViewById(R.id.user_imagethree_Btn); ;
        imageFour=(ImageView)findViewById(R.id.user_imagefour); ;
        imageFourBtn=(ImageView)findViewById(R.id.user_imagefour_Btn); ;
        imageFive=(ImageView)findViewById(R.id.user_imagefive); ;
        imageFiveBtn=(ImageView)findViewById(R.id.user_imagefive_Btn); ;
        imageSix=(ImageView)findViewById(R.id.user_imagesix); ;
        imageSixBtn=(ImageView)findViewById(R.id.user_imagesix_Btn); ;



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
//                        try {
////                            addToFirebase(object, getUid());getUid
//
//                        } catch (JSONException e) {
////                            Toast.makeText(FullscreenActivity.this, e.getMessage() + "", Toast.LENGTH_LONG).show();
//                            e.printStackTrace();
//                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "work");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
